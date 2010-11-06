package org.socialmusicdiscovery.server.api.management.mediaimport;

import com.google.inject.Inject;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.MediaImportManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Path("/mediaimportmodules")
public class MediaImportModuleFacade {

    @Inject
    MediaImportManager mediaImportManager;

    public MediaImportModuleFacade() {
        InjectHelper.injectMembers(this);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<MediaImportStatus> getRunningModules() {
        List<MediaImportStatus> result = new ArrayList<MediaImportStatus>();
        result.addAll(mediaImportManager.getRunningModules());
        return result;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{module}")
    public MediaImportStatus getStatus(@PathParam("module") String module) {
        return mediaImportManager.getModuleStatus(module);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{module}")
    public JSONObject startImport(@PathParam("module") String module) throws JSONException {
        if(mediaImportManager.startImport(module)) {
            return new JSONObject().put("success",true);
        }
        return new JSONObject().put("success",false);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{module}")
    public JSONObject abortImport(@PathParam("module") String module) throws JSONException {
        if(mediaImportManager.abortImport(module)) {
            return new JSONObject().put("success",true);
        }
        return new JSONObject().put("success",false);
    }
}
