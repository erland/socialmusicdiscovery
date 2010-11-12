package org.socialmusicdiscovery.server.business.logic.injections.database;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import java.io.File;
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
            if(properties == null) {
                properties = new HashMap<String,String>();
                properties.put("hibernate.connection.url",getUrl());
                properties.put("hibernate.connection.driver_class",getDriver());
                properties.put("hibernate.connection.username","sa");
                properties.put("hibernate.dialect","org.hibernate.dialect.HSQLDialect");
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
    };

    DatabaseProvider hsqlMemory = new HSQLProvider("jdbc:hsqldb:mem:smd-database");
    DatabaseProvider hsqlDisk = null;

    @Override
    protected void configure() {
    }
    
    @Provides
    @Named("hsql")
    public DatabaseProvider getDiskProvider() {
        if(hsqlDisk == null) {
            String dir = "";
            if(System.getProperty("org.socialmusicdiscovery.server.database.directory") != null) {
                dir = System.getProperty("org.socialmusicdiscovery.server.database.directory");
                if(!dir.endsWith(File.pathSeparator)) {
                    dir+=File.pathSeparator;
                }
            }
            hsqlDisk = new HSQLProvider("jdbc:hsqldb:"+dir+"smd-database");
        }
        return hsqlDisk;
    }

    @Provides
    @Named("hsql-memory")
    public DatabaseProvider getMemoryProvider() {
        return hsqlMemory;
    }
}
