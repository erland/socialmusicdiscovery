package org.socialmusicdiscovery.server.api.management.model.core;

import com.google.gson.annotations.Expose;
import com.google.inject.Inject;
import org.socialmusicdiscovery.server.api.management.model.AbstractCRUDFacade;
import org.socialmusicdiscovery.server.business.logic.TransactionManager;
import org.socialmusicdiscovery.server.business.model.core.RecordingEntity;
import org.socialmusicdiscovery.server.business.repository.core.RecordingRepository;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

/**
 * Provides functionality to create, update, delete and find a specific recording
 */
@Path("/recordings")
public class RecordingFacade extends AbstractCRUDFacade<RecordingEntity, RecordingRepository> {
    @Inject
    private TransactionManager transactionManager;
    /**
     * Search for recordings matching specified search criterias
     *
     * @param name         Exact name of recording
     * @param nameContains The name of the recording has to contain this string
     * @return List of matching recordings
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<RecordingEntity> search(@QueryParam("name") String name, @QueryParam("nameContains") String nameContains) {
        if (name != null) {
            return new CopyHelper().detachedCopy(repository.findByNameWithRelations(name, Arrays.asList("reference"), null), Expose.class);
        } else if (nameContains != null) {
            return new CopyHelper().detachedCopy(repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null), Expose.class);
        } else {
            return new CopyHelper().detachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null), Expose.class);
        }
    }

    /**
     * Get information about a specific recording
     *
     * @param id Identity of recording
     * @return Information about recording
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public RecordingEntity get(@PathParam("id") String id) {
        return new CopyHelper().copy(super.getEntity(id), Expose.class);
    }

    /**
     * Create a new recording
     *
     * @param recording Information about recording to create
     * @return Information about newly created recording including its generated identity
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RecordingEntity create(RecordingEntity recording) {
        try {
            transactionManager.begin();
            return new CopyHelper().copy(super.createEntity(recording), Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Update an existing recording
     *
     * @param id        Identity of recording
     * @param recording Information about recording
     * @return Information about recording
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public RecordingEntity update(@PathParam("id") String id, RecordingEntity recording) {
        try {
            transactionManager.begin();
            return new CopyHelper().copy(super.updateEntity(id, recording), Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Delete an existing recording
     *
     * @param id Identity of recording
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public void delete(@PathParam("id") String id) {
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
}
