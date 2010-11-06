package org.socialmusicdiscovery.server.business.logic.injections.database;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.HashMap;
import java.util.Map;

public class DerbyProviderModule extends AbstractModule {
    class DerbyProvider implements DatabaseProvider {
        HashMap<String, String> properties = null;
        String url;
        public DerbyProvider(String url) {
            this.url = url;
        }
        public Map<String, String> getProperties() {
            if(properties == null) {
                properties = new HashMap<String,String>();
                properties.put("hibernate.connection.url",getUrl());
                properties.put("hibernate.connection.driver_class",getDriver());
                properties.put("hibernate.dialect","org.hibernate.dialect.DerbyDialect");
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
            return "org.apache.derby.jdbc.EmbeddedDriver";
        }
        public void start() {
            try {
                Class.forName(getDriver());
                DriverManager.getConnection(getUrl()+";create=true").close();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        public void stop() {
            try {
                DriverManager.getConnection(getUrl()+";shutdown=true").close();
            } catch (SQLNonTransientConnectionException ex) {
                if (ex.getErrorCode() != 45000) {
                    throw new RuntimeException(ex);
                }
                // Shutdown success
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    };

    private DerbyProvider derbyDisk = new DerbyProvider("jdbc:derby:smd-database");
    private DerbyProvider derbyMemory = new DerbyProvider("jdbc:derby:memory:smd-database");
    @Override
    protected void configure() {
    }
    
    @Provides
    @Named("derby")
    public DatabaseProvider getDiskProvider() {
        return derbyDisk;
    }

    @Provides
    @Named("derby-memory")
    public DatabaseProvider getMemoryProvider() {
        return derbyMemory;
    }
}
