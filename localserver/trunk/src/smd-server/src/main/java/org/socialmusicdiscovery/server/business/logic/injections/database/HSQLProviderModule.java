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

public class HSQLProviderModule extends AbstractModule {
    class HSQLProvider implements DatabaseProvider {
        private String url;
        HashMap<String, String> properties = null;

        public HSQLProvider(String url) {
            this.url = url;
        }

        public Map<String, String> getProperties() {
            if (properties == null) {
                properties = new HashMap<String, String>();
                properties.put("hibernate.connection.url", getUrl());
                properties.put("hibernate.connection.driver_class", getDriver());
                properties.put("hibernate.connection.username", "sa");
                properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
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
            return "org.hsqldb.jdbcDriver";
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
            return DriverManager.getConnection(getUrl(), "sa", "");
        }
    }

    ;

    DatabaseProvider hsqlMemory = new HSQLProvider("jdbc:hsqldb:mem:smd-database");
    DatabaseProvider hsqlDisk = null;

    @Override
    protected void configure() {
    }

    /**
     * Instance persisted to disk
     * @return DatabaseProvider instance
     */
    @Provides
    @Named("hsql")
    public DatabaseProvider getDiskProvider() {
        if (hsqlDisk == null) {
            String dir = "";
            if (System.getProperty("org.socialmusicdiscovery.server.database.directory") != null) {
                dir = System.getProperty("org.socialmusicdiscovery.server.database.directory");
                if (!dir.endsWith(File.separator)) {
                    dir += File.separator;
                }
            }
            hsqlDisk = new HSQLProvider("jdbc:hsqldb:" + dir + "smd-database");
        }
        return hsqlDisk;
    }

    /**
     * Instance only stored in memory, not persisted to disk
     * @return DatabaseProvider instance
     */
    @Provides
    @Named("hsql-memory")
    public DatabaseProvider getMemoryProvider() {
        return hsqlMemory;
    }

    /**
     * Instance persisted to disk and preloaded with sample test data
     * @return DatabaseProvider instance
     */
    @Provides
    @Named("hsql-test")
    public DatabaseProvider getDiskTestProvider() {
        return getDiskProvider();
    }

    /**
     * Instance only stored in memory, not persisted to disk, but preloaded with sample test data
     * @return DatabaseProvider instance
     */
    @Provides
    @Named("hsql-memory-test")
    public DatabaseProvider getMemoryTestProvider() {
        return hsqlMemory;
    }
}
