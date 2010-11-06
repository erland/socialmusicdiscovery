package org.socialmusicdiscovery.server.business.logic.injections.database;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

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
            if(properties == null) {
                properties = new HashMap<String,String>();
                properties.put("hibernate.connection.url",getUrl());
                properties.put("hibernate.connection.driver_class",getDriver());
                if(System.getProperty("mysql.username") != null) {
                    properties.put("hibernate.connection.username",System.getProperty("mysql.username"));
                }else {
                    properties.put("hibernate.connection.username","");
                }
                if(System.getProperty("mysql.password") != null) {
                    properties.put("hibernate.connection.password",System.getProperty("mysql.password"));
                }
                properties.put("hibernate.dialect","org.hibernate.dialect.MySQLInnoDBDialect");
                if(System.getProperty("hibernate.show_sql") != null) {
                    properties.put("hibernate.show_sql",System.getProperty("hibernate.show_sql"));
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
    };

    DatabaseProvider mysqlSBS = null;
    DatabaseProvider mysqlStandalone = null;

    @Override
    protected void configure() {
    }

    private String getMySQLHost() {
        if(System.getProperty("mysql.host") == null) {
            return System.getProperty("squeezeboxserver.host");
        }else {
            return System.getProperty("mysql.host");
        }
    }
    @Provides
    @Named("mysql-sbs")
    public DatabaseProvider getSBSProvider() {
        if(mysqlSBS == null) {
            mysqlSBS = new MySQLProvider("jdbc:mysql://"+getMySQLHost()+":9092/smd?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf-8");
        }
        return mysqlSBS;
    }

    @Provides
    @Named("mysql-standalone")
    public DatabaseProvider getStandaloneProvider() {
        if(mysqlStandalone == null) {
            mysqlStandalone = new MySQLProvider("jdbc:mysql://"+getMySQLHost()+"/smd?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf-8");
        }
        return mysqlStandalone;
    }
}
