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

package org.socialmusicdiscovery.server.plugins.mediaimport.dropdatabase;

import com.google.inject.Inject;
import liquibase.ClassLoaderFileOpener;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import org.socialmusicdiscovery.server.api.mediaimport.MediaImporter;
import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.injections.database.DatabaseProvider;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Special importer that drops all database contents, only useful during beta testing if you like to restart everything
 */
public class DropDatabase implements MediaImporter {
    public DropDatabase() {
        InjectHelper.injectMembers(this);
    }

    @Override
    public String getId() {
        return "dropdatabase";
    }

    @Override
    public void execute(ProcessingStatusCallback progressHandler) {
        String database = InjectHelper.instanceWithName(String.class, "org.socialmusicdiscovery.server.database");
        DatabaseProvider provider = InjectHelper.instanceWithName(DatabaseProvider.class, database);
        try {
            progressHandler.progress(getId(),"Deleting database contents",1L,2L);
            Connection connection = provider.getConnection();
            Liquibase liquibase = new Liquibase("org/socialmusicdiscovery/server/database/smd-database-drop.xml", new
                    ClassLoaderFileOpener(),
                    connection);
            liquibase.update("");
            progressHandler.progress(getId(), "Creating fresh database", 2L, 2L);
            liquibase = new Liquibase("org/socialmusicdiscovery/server/database/smd-database.changelog.xml", new
                    ClassLoaderFileOpener(),
                    connection);
            liquibase.update("");
            progressHandler.finished(getId());
        } catch (LiquibaseException e) {
            progressHandler.failed(getId(),e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            progressHandler.failed(getId(),e.getMessage());
        }
    }

    @Override
    public void abort() {
    }
}
