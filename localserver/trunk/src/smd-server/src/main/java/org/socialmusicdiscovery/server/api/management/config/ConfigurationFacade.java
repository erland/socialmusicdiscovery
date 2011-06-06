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

package org.socialmusicdiscovery.server.api.management.config;

import com.google.gson.annotations.Expose;
import org.codehaus.jettison.json.JSONException;
import org.socialmusicdiscovery.server.api.management.AbstractCRUDFacade;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.config.MergedConfigurationManager;
import org.socialmusicdiscovery.server.business.logic.config.PersistentConfigurationManager;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;
import org.socialmusicdiscovery.server.business.repository.config.ConfigurationParameterRepository;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

/**
 * Provides functionality for setting and getting configuration parameters
 */
@Path("/configurations")
public class ConfigurationFacade extends AbstractCRUDFacade<String, ConfigurationParameterEntity, ConfigurationParameterRepository> {

    public ConfigurationFacade() {
        InjectHelper.injectMembers(this);
    }
    /**
     * Get a list of all available configuration parameters
     *
     * @return A list of all available configuration parameters
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ConfigurationParameterEntity> getParameters(@QueryParam("path") String path) {
        if(path!=null) {
            return new CopyHelper().detachedCopy(new MergedConfigurationManager(new PersistentConfigurationManager()).getParametersByPath(path), Expose.class);
        }else {
            return new CopyHelper().detachedCopy(new MergedConfigurationManager(new PersistentConfigurationManager()).getParameters(), Expose.class);
        }
    }

    /**
     * Get information about a specific configuration parameter
     *
     * @param id The identity of the configuration parameter
     * @return The information about the configuration parameter or null if the parameter doesn't exist
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public ConfigurationParameter getParameter(@PathParam("id") String id) {
        return new CopyHelper().copy(new MergedConfigurationManager(new PersistentConfigurationManager()).getParameter(id), Expose.class);
    }

    /**
     * Create a new configuration parameter
     *
     * @param parameter The identity of the media importer module
     * @return The information of the configuration parameter
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationParameterEntity createParameter(ConfigurationParameterEntity parameter) throws JSONException {
        try {
            transactionManager.begin();
            return new CopyHelper().copy(super.createEntity(parameter), Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Updates a configuration parameter
     *
     * @param id The identity of the media importer module
     * @return The information of the configuration parameter
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public ConfigurationParameter updateParameter(@PathParam("id") String id, ConfigurationParameterEntity entity) throws JSONException {
        try {
            transactionManager.begin();
            if (id != null && !entity.getId().equals(id)) {
                entity.setId(id);
            }
            return new CopyHelper().copy(super.updateEntity(entity), Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Deletes an existing configuration parameter
     *
     * @param id The identity of the configuration parameter
     */
    @DELETE
    @Path("/{id}")
    public void deleteParameter(@PathParam("id") String id) throws JSONException {
        try {
            transactionManager.begin();
            super.deleteEntity(id);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Deletes all existing configuration parameters and reset configuration to default
     */
    @DELETE
    public void deleteAllParameters() throws JSONException {
        try {
            transactionManager.begin();
            Collection<ConfigurationParameterEntity> configurations = repository.findAll();
            for (ConfigurationParameterEntity configuration : configurations) {
                repository.remove(configuration);
            }
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
    }
}
