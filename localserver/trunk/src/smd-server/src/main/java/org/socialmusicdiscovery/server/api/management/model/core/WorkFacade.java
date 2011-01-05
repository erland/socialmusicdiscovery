package org.socialmusicdiscovery.server.api.management.model.core;

import org.socialmusicdiscovery.server.api.management.model.AbstractCRUDFacade;
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
            return CopyHelper.createDetachedCopy(repository.findByNameWithRelations(name, Arrays.asList("reference"), null));
        } else if (nameContains != null) {
            return CopyHelper.createDetachedCopy(repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null));
        } else if (release != null) {
            return CopyHelper.createDetachedCopy(repository.findByReleaseWithRelations(release, Arrays.asList("reference"), null));
        } else if (artist != null) {
            return CopyHelper.createDetachedCopy(repository.findByArtistWithRelations(artist, Arrays.asList("reference"), null));
        } else {
            return CopyHelper.createDetachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null));
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
        return super.getEntity(id);
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
        return super.createEntity(work);
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
        return super.updateEntity(id, work);
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
        super.deleteEntity(id);
    }
}
