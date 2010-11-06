package org.socialmusicdiscovery.server.business.logic.injections.database;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

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
            if(properties == null) {
                properties = new HashMap<String,String>();
                properties.put("hibernate.connection.url",getUrl());
                properties.put("hibernate.connection.driver_class",getDriver());
                properties.put("hibernate.dialect","org.hibernate.dialect.H2Dialect");
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
    };

    DatabaseProvider h2Memory = new H2Provider("jdbc:h2:mem:smd-database");
    DatabaseProvider h2Disk = new H2Provider("jdbc:h2:file:smd-database");
    DatabaseProvider h2Trace = new H2Provider("jdbc:h2:file:smd-database;TRACE_LEVEL_FILE=2");
    @Override
    protected void configure() {
    }
    
    @Provides
    @Named("h2")
    public DatabaseProvider getDiskProvider() {
        return h2Disk;
    }

    @Provides
    @Named("h2-trace")
    public DatabaseProvider getTraceProvider() {
        return h2Trace;
    }

    @Provides
    @Named("h2-memory")
    public DatabaseProvider getMemoryProvider() {
        return h2Memory;
    }
}
