package org.socialmusicdiscovery.server.api.management.model.core;

import org.socialmusicdiscovery.server.api.management.model.AbstractCRUDFacade;
import org.socialmusicdiscovery.server.business.logic.DetachHelper;
import org.socialmusicdiscovery.server.business.model.core.RecordingSessionEntity;
import org.socialmusicdiscovery.server.business.repository.core.RecordingSessionRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

/**
 * Provides functionality to create, update, delete and find a specific recording session
 */
@Path("/recordingsessions")
public class RecordingSessionFacade extends AbstractCRUDFacade<RecordingSessionEntity, RecordingSessionRepository> {
    /**
     * Search for recordings sessions
     *
     * @return List of recording sessions
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<RecordingSessionEntity> search() {
        return DetachHelper.createDetachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null));
    }

    /**
     * Get information about a specific recording session
     *
     * @param id Identity of recording session
     * @return Information about recording session
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public RecordingSessionEntity get(@PathParam("id") String id) {
        return super.getEntity(id);
    }

    /**
     * Create a new recording session
     *
     * @param recordingSession Information about recording session to create
     * @return Information about newly created recording session including its generated identity
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RecordingSessionEntity create(RecordingSessionEntity recordingSession) {
        return super.createEntity(recordingSession);
    }

    /**
     * Update an existing recording session
     *
     * @param id               Identity of recording session
     * @param recordingSession Information about recording session
     * @return Information about recording session
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public RecordingSessionEntity update(@PathParam("id") String id, RecordingSessionEntity recordingSession) {
        return super.updateEntity(id, recordingSession);
    }

    /**
     * Delete an existing recording session
     *
     * @param id Identity of recording session
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public void delete(@PathParam("id") String id) {
        super.deleteEntity(id);
    }
}
