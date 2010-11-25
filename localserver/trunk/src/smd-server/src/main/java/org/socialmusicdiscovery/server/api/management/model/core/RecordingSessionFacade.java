package org.socialmusicdiscovery.server.api.management.model.core;

import org.socialmusicdiscovery.server.api.management.model.BaseCRUDFacade;
import org.socialmusicdiscovery.server.business.logic.DetachHelper;
import org.socialmusicdiscovery.server.business.model.core.RecordingSession;
import org.socialmusicdiscovery.server.business.repository.core.RecordingSessionRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

@Path("/recordingsessions")
public class RecordingSessionFacade extends BaseCRUDFacade<RecordingSession, RecordingSessionRepository> {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<RecordingSession> search() {
        return DetachHelper.createDetachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public RecordingSession get(@PathParam("id") String id) {
        return super.get(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public RecordingSession create(RecordingSession recordingSession) {
        return super.create(recordingSession);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public RecordingSession update(@PathParam("id") String id, RecordingSession recordingSession) {
        return super.update(id, recordingSession);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public void delete(@PathParam("id") String id) {
        super.delete(id);
    }
}
