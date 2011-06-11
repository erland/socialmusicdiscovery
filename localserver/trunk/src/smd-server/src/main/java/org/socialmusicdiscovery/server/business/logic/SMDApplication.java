/*
 *  Copyright 2010-2011, Social Music Discovery project
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of Social Music Discovery project nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.socialmusicdiscovery.server.business.logic;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.socialmusicdiscovery.server.api.management.mediaimport.MediaImportStatus;
import org.socialmusicdiscovery.server.business.logic.injections.database.DatabaseProvider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.*;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class SMDApplication {
    @Inject
    private EntityManagerFactory emFactory;

    @Inject
    private EntityManager em;

    @Inject
    MediaImportManager mediaImportManager;

    @Inject
    PluginManager pluginManager;

    @Inject
    @Named("mediaimport")
    ExecutorService mediaImportService;

    private boolean shutdownRequest = false;
    private Thread mainThread;

    public static void main(String[] args) throws IOException {
        String customStdOut = System.getProperty("org.socialmusicdiscovery.server.stdout");
        String customStdErr = System.getProperty("org.socialmusicdiscovery.server.stderr");
        String isDaemon = System.getProperty("org.socialmusicdiscovery.server.daemon");
        if(isDaemon != null && isDaemon.equalsIgnoreCase("true")) {
            System.in.close();
        }
        if (customStdOut != null) {
            try {
                System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(customStdOut, true))));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if(isDaemon != null && isDaemon.equalsIgnoreCase("true")) {
            System.out.close();
        }
        if (customStdErr != null) {
            try {
                if (customStdOut != null && customStdErr.equals(customStdOut)) {
                    System.setErr(System.out);
                } else {
                    System.setErr(new PrintStream(new BufferedOutputStream(new FileOutputStream(customStdErr, true))));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if(isDaemon != null && isDaemon.equalsIgnoreCase("true")) {
            System.err.close();
        }
        new SMDApplication();
    }
    
	public void shutdown() throws InterruptedException {
		
        for (MediaImportStatus module : mediaImportManager.getRunningModules()) {
            mediaImportManager.abortImport(module.getModule());
        }
        mediaImportService.shutdown();
        if (!mediaImportService.awaitTermination(10, TimeUnit.SECONDS)) {
            mediaImportService.shutdownNow();
        }
        // Initialize all activated plugins
        pluginManager.stopAll();

        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emFactory != null && emFactory.isOpen()) {
            emFactory.close();
        }
	}
	
    public SMDApplication() {
        mainThread = Thread.currentThread();

        try {
            DatabaseProvider provider = null;
            String database = InjectHelper.instanceWithName(String.class, "org.socialmusicdiscovery.server.database");
            if (database != null) {
                provider = InjectHelper.instanceWithName(DatabaseProvider.class, database);
                if (provider == null) {
                    throw new RuntimeException("No database provider exists for: " + database);
                }
            } else {
                throw new RuntimeException("No database provider configured");
            }
            provider.start();
            Connection connection = provider.getConnection();
            Liquibase liquibase = new Liquibase("org/socialmusicdiscovery/server/database/smd-database.changelog.xml", new
                    ClassLoaderResourceAccessor(),
                    new JdbcConnection(connection));
            if (System.getProperty("liquibase") == null || !System.getProperty("liquibase").equals("false")) {
                // If database is locked, retry 10 times before we give up and force the lock to be released
                for(int i=0;i<10 && liquibase.listLocks().length>0;i++) {
                    Thread.sleep(1000);
                }
                if(liquibase.listLocks().length>0) {
                    liquibase.forceReleaseLocks();
                }
                liquibase.update("");
            }

            InjectHelper.injectMembers(this);

            // Add shutdown hook to exit cleanly after receiving a CTRL-C
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
                        System.out.println("Initiating shutdown...");
                        shutdownRequest = true;
                        mainThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});

            // Initialize all installed plugins
            pluginManager.startAll();

            System.out.println("Hit Ctrl+C to stop it...");
            while(!shutdownRequest) {
                try {
                    Thread.sleep(1000);
                }catch(InterruptedException e) {
                    // Do nothing
                }
            }

            System.out.println("\n\nExiting...\n");

            this.shutdown();

            provider.stop();
            System.out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
