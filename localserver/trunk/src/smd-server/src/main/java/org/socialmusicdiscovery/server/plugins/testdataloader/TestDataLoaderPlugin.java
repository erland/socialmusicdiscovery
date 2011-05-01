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

package org.socialmusicdiscovery.server.plugins.testdataloader;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
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
                execute(connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PluginException(getClass().getSimpleName() + " failure", e);
        }
        return false;
    }

    void execute(Connection connection) throws PluginException {
        try {
            Liquibase liquibase = new Liquibase("org/socialmusicdiscovery/server/database/sampledata/smd-database.sampledata.xml", new
                    ClassLoaderResourceAccessor(),
                    new JdbcConnection(connection));
            liquibase.update("");
        } catch (LiquibaseException e) {
            e.printStackTrace();
            throw new PluginException(getClass().getSimpleName() + " failure", e);
        }
    }
}
