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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class MySQLProviderModule extends AbstractModule {
    class MySQLProvider implements DatabaseProvider {
        private String url;
        HashMap<String, String> properties = null;

        public MySQLProvider(String url) {
            this.url = url;
        }

        public Map<String, String> getProperties() {
            if (properties == null) {
                properties = new HashMap<String, String>();
                properties.put("hibernate.connection.url", getUrl());
                properties.put("hibernate.connection.driver_class", getDriver());
                if (System.getProperty("mysql.username") != null) {
                    properties.put("hibernate.connection.username", System.getProperty("mysql.username"));
                } else {
                    properties.put("hibernate.connection.username", "");
                }
                if (System.getProperty("mysql.password") != null) {
                    properties.put("hibernate.connection.password", System.getProperty("mysql.password"));
                }
                properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect");
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
            return "com.mysql.jdbc.Driver";
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
            return DriverManager.getConnection(getUrl(), System.getProperty("mysql.username"), System.getProperty("mysql.password"));
        }
    }

    ;

    DatabaseProvider mysqlSBS = null;
    DatabaseProvider mysqlStandalone = null;

    @Override
    protected void configure() {
    }

    private String getMySQLHost() {
        if (System.getProperty("mysql.host") == null) {
            return System.getProperty("squeezeboxserver.host");
        } else {
            return System.getProperty("mysql.host");
        }
    }

    @Provides
    @Named("mysql-sbs")
    public DatabaseProvider getSBSProvider() {
        if (mysqlSBS == null) {
            mysqlSBS = new MySQLProvider("jdbc:mysql://" + getMySQLHost() + ":9092/smd?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf-8");
        }
        return mysqlSBS;
    }

    @Provides
    @Named("mysql-sbs-test")
    public DatabaseProvider getSBSTestProvider() {
        return getSBSProvider();
    }

    @Provides
    @Named("mysql-standalone")
    public DatabaseProvider getStandaloneProvider() {
        if (mysqlStandalone == null) {
            mysqlStandalone = new MySQLProvider("jdbc:mysql://" + getMySQLHost() + "/smd?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf-8");
        }
        return mysqlStandalone;
    }

    @Provides
    @Named("mysql-standalone-test")
    public DatabaseProvider getStandaloneTestProvider() {
        return getStandaloneProvider();
    }
}
