package org.socialmusicdiscovery.server.business.logic;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import liquibase.ClassLoaderFileOpener;
import liquibase.Liquibase;
import org.socialmusicdiscovery.server.api.management.mediaimport.MediaImportStatus;
import org.socialmusicdiscovery.server.business.logic.injections.database.DatabaseProvider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class SMDApplication {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy");

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

    public static void main(String[] args) {
        String customStdOut = System.getProperty("org.socialmusicdiscovery.server.stdout");
        String customStdErr = System.getProperty("org.socialmusicdiscovery.server.stderr");
        if (customStdOut != null) {
            try {
                System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(customStdOut, true))));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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
        }
        new SMDApplication();
    }

    public SMDApplication() {
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
                    ClassLoaderFileOpener(),
                    connection);
            if (System.getProperty("liquibase") == null || !System.getProperty("liquibase").equals("false")) {
                liquibase.update("");
            }

            InjectHelper.injectMembers(this);

            // Initialize all installed plugins
            pluginManager.startAll();

            System.out.println("Hit q+enter to stop it...");
            while (System.in.read() != 'q') {
            }
            ;

            System.out.println("\n\nExiting...\n");

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
            provider.stop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
