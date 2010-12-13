package org.socialmusicdiscovery.server.api.management.model.core;

import org.socialmusicdiscovery.server.api.management.model.AbstractCRUDFacade;
import org.socialmusicdiscovery.server.business.logic.DetachHelper;
import org.socialmusicdiscovery.server.business.model.core.RecordingSessionEntity;
import org.socialmusicdiscovery.server.business.repository.core.RecordingSessionRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

@Path("/recordingsessions")
public class RecordingSessionFacade extends AbstractCRUDFacade<RecordingSessionEntity, RecordingSessionRepository> {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<RecordingSessionEntity> search() {
        return DetachHelper.createDetachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public RecordingSessionEntity get(@PathParam("id") String id) {
        return super.getEntity(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RecordingSessionEntity create(RecordingSessionEntity recordingSession) {
        return super.createEntity(recordingSession);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public RecordingSessionEntity update(@PathParam("id") String id, RecordingSessionEntity recordingSession) {
        return super.updateEntity(id, recordingSession);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public void delete(@PathParam("id") String id) {
        super.deleteEntity(id);
    }
}
