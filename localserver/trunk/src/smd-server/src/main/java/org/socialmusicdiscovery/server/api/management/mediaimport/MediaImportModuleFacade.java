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

package org.socialmusicdiscovery.server.api.management.mediaimport;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.codehaus.jettison.json.JSONException;
import org.socialmusicdiscovery.server.api.OperationStatus;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.MediaImportManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * Provides functionality for managing media importer modules and post processing actions
 */
@Path("/mediaimportmodules")
public class MediaImportModuleFacade {

    @Inject
    MediaImportManager mediaImportManager;

    public MediaImportModuleFacade() {
        InjectHelper.injectMembers(this);
    }

    /**
     * Get a list of all media importer modules currently executing
     *
     * @return A list of all executing media importer modules, an empty list of no modules are executing
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<MediaImportStatus> getRunningModules() {
        List<MediaImportStatus> result = new ArrayList<MediaImportStatus>();
        result.addAll(mediaImportManager.getRunningModules());
        return result;
    }

    /**
     * Get status of the specified media importer module
     *
     * @param module The identity of the media importer module
     * @return The current status or null if the module isn't executing
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{module}")
    public MediaImportStatus getStatus(@PathParam("module") String module) {
        return mediaImportManager.getModuleStatus(module);
    }

    /**
     * Start an import using the specified media importer module
     *
     * @param module The identity of the media importer module
     * @return true if the module can be started, false if the module doesn't exist or is currently executing
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{module}")
    public OperationStatus startImport(@PathParam("module") String module, JsonObject parameters) throws JSONException {
        Map<String,String> parametersMap = new HashMap<String,String>();
        for (Map.Entry<String, JsonElement> entry : parameters.entrySet()) {
            parametersMap.put(entry.getKey(), entry.getValue().getAsString());
        }
        if (mediaImportManager.startImport(module, parametersMap)) {
            return new OperationStatus(true);
        } else {
            return new OperationStatus(false);
        }
    }

    /**
     * Abort an import of the specified media importer module
     *
     * @param module The identity of the media importer module
     * @return true if the import was successfully aborted, false if the media importer module doesn't exist
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{module}")
    public OperationStatus abortImport(@PathParam("module") String module) throws JSONException {
        if (mediaImportManager.abortImport(module)) {
            return new OperationStatus(true);
        } else {
            return new OperationStatus(false);
        }
    }
}
