package org.socialmusicdiscovery.server.plugins.testdataloader;

import liquibase.ClassLoaderFileOpener;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import org.socialmusicdiscovery.server.api.plugin.AbstractPlugin;
import org.socialmusicdiscovery.server.api.plugin.Plugin;
import org.socialmusicdiscovery.server.api.plugin.PluginException;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.injections.database.DatabaseProvider;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDataLoaderPlugin extends AbstractPlugin {
    @Override
    public int getStartPriority() {
        return Plugin.START_PRIORITY_EARLY;
    }

    @Override
    public boolean start() throws PluginException {
        String database = InjectHelper.instanceWithName(String.class, "org.socialmusicdiscovery.server.database");
        DatabaseProvider provider = InjectHelper.instanceWithName(DatabaseProvider.class, database);
        try {
            Connection connection = provider.getConnection();
            if (database != null && (database.endsWith("-test"))) {
                Liquibase liquibase = new Liquibase("org/socialmusicdiscovery/server/database/sampledata/smd-database.sampledata.xml", new
                        ClassLoaderFileOpener(),
                        connection);
                liquibase.update("");
            }
        } catch (LiquibaseException e) {
            throw new PluginException(getClass().getSimpleName() + " failure", e);
        } catch (SQLException e) {
            throw new PluginException(getClass().getSimpleName() + " failure", e);
        }
        return false;
    }
}
