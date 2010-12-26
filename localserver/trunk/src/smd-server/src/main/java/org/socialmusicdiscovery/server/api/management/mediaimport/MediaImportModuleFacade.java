package org.socialmusicdiscovery.server.api.management.mediaimport;

import com.google.inject.Inject;
import org.codehaus.jettison.json.JSONException;
import org.socialmusicdiscovery.server.api.OperationStatus;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.MediaImportManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{module}")
    public OperationStatus startImport(@PathParam("module") String module) throws JSONException {
        if (mediaImportManager.startImport(module)) {
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
