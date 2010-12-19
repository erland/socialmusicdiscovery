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

    @Provides
    @Named("h2")
    public DatabaseProvider getDiskProvider() {
        if (h2Disk == null) {
            h2Disk = new H2Provider("jdbc:h2:file:" + getDatabaseFile());
        }
        return h2Disk;
    }

    @Provides
    @Named("h2-trace")
    public DatabaseProvider getTraceProvider() {
        if (h2Trace == null) {
            h2Trace = new H2Provider("jdbc:h2:file:" + getDatabaseFile() + ";TRACE_LEVEL_FILE=2");
        }
        return h2Trace;
    }

    @Provides
    @Named("h2-memory")
    public DatabaseProvider getMemoryProvider() {
        return h2Memory;
    }

    @Provides
    @Named("h2-memory-test")
    public DatabaseProvider getMemoryTestProvider() {
        return h2Memory;
    }

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
