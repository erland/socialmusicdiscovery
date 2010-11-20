package org.socialmusicdiscovery.server.business.logic.injections.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * All database providers needs to implement this interface
 */
public interface DatabaseProvider {
    /**
     * Get the list of properties that should be overridden when creating the {@link javax.persistence.EntityManagerFactory}}
     *
     * @return A map of properties
     */
    Map<String, String> getProperties();

    /**
     * The class name of the JDBC driver to use for this provider
     *
     * @return The class name
     */
    String getDriver();

    /**
     * The URL to use when accessing this provider
     *
     * @return The JDBC database url
     */
    String getUrl();

    /**
     * Called initially before the first connection is established with the provider
     */
    void start();

    /**
     * Called at the end after the last connection towards the provider has been closed
     */
    void stop();

    /**
     * Called to get a new connection, the caller is responsible to call close() on the connection
     * when it has finished using it
     */
    Connection getConnection() throws SQLException;
}
