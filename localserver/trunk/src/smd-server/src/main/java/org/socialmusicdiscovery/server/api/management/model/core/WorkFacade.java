package org.socialmusicdiscovery.server.api.management.model.core;

import com.google.gson.annotations.Expose;
import com.google.inject.Inject;
import org.socialmusicdiscovery.server.api.management.model.AbstractCRUDFacade;
import org.socialmusicdiscovery.server.business.logic.TransactionManager;
import org.socialmusicdiscovery.server.business.model.core.WorkEntity;
import org.socialmusicdiscovery.server.business.repository.core.WorkRepository;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

/**
 * Provides functionality to create, update, delete and find a specific work
 */
@Path("/works")
public class WorkFacade extends AbstractCRUDFacade<WorkEntity, WorkRepository> {
    @Inject
    private TransactionManager transactionManager;
    /**
     * Search for work matching the search criterias
     *
     * @param name         Exact name of the work
     * @param nameContains The name of the work has to contain this string
     * @param release      Identity of a release which the work is part of
     * @param artist       Identity of an artist which contributes to the work
     * @return List of matching works
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<WorkEntity> search(@QueryParam("name") String name, @QueryParam("nameContains") String nameContains, @QueryParam("release") String release, @QueryParam("artist") String artist) {
        if (name != null) {
            return new CopyHelper().detachedCopy(repository.findByNameWithRelations(name, Arrays.asList("reference"), null), Expose.class);
        } else if (nameContains != null) {
            return new CopyHelper().detachedCopy(repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null), Expose.class);
        } else if (release != null) {
            return new CopyHelper().detachedCopy(repository.findByReleaseWithRelations(release, Arrays.asList("reference"), null), Expose.class);
        } else if (artist != null) {
            return new CopyHelper().detachedCopy(repository.findByArtistWithRelations(artist, Arrays.asList("reference"), null), Expose.class);
        } else {
            return new CopyHelper().detachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null), Expose.class);
        }
    }

    /**
     * Get information about a specific work
     *
     * @param id Identity of the work
     * @return Information about the work
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public WorkEntity get(@PathParam("id") String id) {
        return new CopyHelper().copy(super.getEntity(id), Expose.class);
    }

    /**
     * Create a new work
     *
     * @param work Information about work to be created
     * @return Information about newly created work including its generated identity
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkEntity create(WorkEntity work) {
        try {
            transactionManager.begin();
            return new CopyHelper().copy(super.createEntity(work), Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Update an existing work
     *
     * @param id   Identity of the work
     * @param work Updated information about the work
     * @return Updated information about the work
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public WorkEntity update(@PathParam("id") String id, WorkEntity work) {
        try {
            transactionManager.begin();
            return new CopyHelper().copy(super.updateEntity(id, work), Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Delete an existing work
     *
     * @param id Identity of the work
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
