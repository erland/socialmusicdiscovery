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

package org.socialmusicdiscovery.server.business.logic.injections.database;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class H2ProviderModule extends AbstractModule {
    class H2Provider implements DatabaseProvider {
        private String url;
        HashMap<String, String> properties = null;

        public H2Provider(String url) {
            this.url = url;
        }

        public Map<String, String> getProperties() {
            if (properties == null) {
                properties = new HashMap<String, String>();
                properties.put("hibernate.connection.url", getUrl());
                properties.put("hibernate.connection.driver_class", getDriver());
                properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
                Enumeration systemProperties = System.getProperties().propertyNames();
                while (systemProperties.hasMoreElements()) {
                    Object property = systemProperties.nextElement();
                    if (property.toString().startsWith("hibernate.") && System.getProperty(property.toString()) != null) {
                        properties.put(property.toString(), System.getProperty(property.toString()));
                    }
                }
            }
            return properties;
        }

        public String getUrl() {
            return url;
        }

        public String getDriver() {
            return "org.h2.Driver";
        }

        public void start() {
            try {
                Class.forName(getDriver());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public void stop() {
            // Do nothing
        }

        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(getUrl());
        }
    }

    ;

    DatabaseProvider h2Memory = new H2Provider("jdbc:h2:mem:smd-database");
    DatabaseProvider h2Disk = null;
    DatabaseProvider h2Trace = null;

    @Override
    protected void configure() {
    }

    /**
     * Instance persisted to disk
     * @return DatabaseProvider instance
     */
    @Provides
    @Named("h2")
    public DatabaseProvider getDiskProvider() {
        if (h2Disk == null) {
            h2Disk = new H2Provider("jdbc:h2:file:" + getDatabaseFile());
        }
        return h2Disk;
    }

    /**
     * Instance persisted to disk with trace logging enabled, this is useful to use during performance measurement. A sql file with a statistics
     * section can be generated based on the generated trace.db file by running:<br/>
     * java -cp h2*.jar org.h2.tools.ConvertTraceFile -traceFile smd-database.trace.db -script smd-database.sql<br/>
     * The h2 jar file can be found in maven repository, for example in:<br/>
     * $HOME/.m2/repository/com/h2database/h2/1.2.144/h2-1.2.144.jar
     * @return DatabaseProvider instance
     */
    @Provides
    @Named("h2-trace")
    public DatabaseProvider getTraceProvider() {
        if (h2Trace == null) {
            h2Trace = new H2Provider("jdbc:h2:file:" + getDatabaseFile() + ";TRACE_LEVEL_FILE=2");
        }
        return h2Trace;
    }

    /**
     * Instance only stored in memory, not persisted to disk
     * @return DatabaseProvider instance
     */
    @Provides
    @Named("h2-memory")
    public DatabaseProvider getMemoryProvider() {
        return h2Memory;
    }

    /**
     * Instance only stored in memory, not persisted to disk, but preloaded with sample test data
     * @return DatabaseProvider instance
     */
    @Provides
    @Named("h2-memory-test")
    public DatabaseProvider getMemoryTestProvider() {
        return h2Memory;
    }

    /**
     * Instance persisted to disk and preloaded with sample test data
     * @return DatabaseProvider instance
     */
    @Provides
    @Named("h2-test")
    public DatabaseProvider getDiskTestProvider() {
        return getDiskProvider();
    }

    private String getDatabaseFile() {
        String dir = "";
        if (System.getProperty("org.socialmusicdiscovery.server.database.directory") != null) {
            dir = System.getProperty("org.socialmusicdiscovery.server.database.directory");
            if (!dir.endsWith(File.separator)) {
                dir += File.separator;
            }
        }
        return dir + "smd-database";
    }
}
