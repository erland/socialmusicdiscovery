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
import java.sql.SQLNonTransientConnectionException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Database provider for Apache Derby database
 */
public class DerbyProviderModule extends AbstractModule {
    class DerbyProvider implements DatabaseProvider {
        HashMap<String, String> properties = null;
        String url;

        public DerbyProvider(String url) {
            this.url = url;
        }

        public Map<String, String> getProperties() {
            if (properties == null) {
                properties = new HashMap<String, String>();
                properties.put("hibernate.connection.url", getUrl());
                properties.put("hibernate.connection.driver_class", getDriver());
                properties.put("hibernate.dialect", "org.hibernate.dialect.DerbyDialect");
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
            return "org.apache.derby.jdbc.EmbeddedDriver";
        }

        public void start() {
            try {
                Class.forName(getDriver());
                DriverManager.getConnection(getUrl() + ";create=true").close();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public void stop() {
            try {
                DriverManager.getConnection(getUrl() + ";shutdown=true").close();
            } catch (SQLNonTransientConnectionException ex) {
                if (ex.getErrorCode() != 45000) {
                    throw new RuntimeException(ex);
                }
                // Shutdown success
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(getUrl());
        }
    }

    ;

    private DerbyProvider derbyDisk = null;
    private DerbyProvider derbyMemory = new DerbyProvider("jdbc:derby:memory:smd-database");

    @Override
    protected void configure() {
    }

    /**
     * Instance persisted to disk
     * @return DatabaseProvider instance
     */
    @Provides
    @Named("derby")
    public DatabaseProvider getDiskProvider() {
        if (derbyDisk == null) {
            String dir = "";
            if (System.getProperty("org.socialmusicdiscovery.server.database.directory") != null) {
                dir = System.getProperty("org.socialmusicdiscovery.server.database.directory");
                if (!dir.endsWith(File.separator)) {
                    dir += File.separator;
                }
            }
            derbyDisk = new DerbyProvider("jdbc:derby:smd-database");
        }
        return derbyDisk;
    }

    /**
     * Instance only stored in memory, not persisted to disk
     * @return DatabaseProvider instance
     */
    @Provides
    @Named("derby-memory")
    public DatabaseProvider getMemoryProvider() {
        return derbyMemory;
    }

    /**
     * Instance only stored in memory, not persisted to disk, but preloaded with sample test data
     * @return DatabaseProvider instance
     */
    @Provides
    @Named("derby-memory-test")
    public DatabaseProvider getMemoryTestProvider() {
        return derbyMemory;
    }

    /**
     * Instance persisted to disk and preloaded with sample test data
     * @return DatabaseProvider instance
     */
    @Provides
    @Named("derby-test")
    public DatabaseProvider getDiskTestProvider() {
        return getDiskProvider();
    }
}
