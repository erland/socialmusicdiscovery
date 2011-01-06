package org.socialmusicdiscovery.server.api.management.model.core;

import org.socialmusicdiscovery.server.api.management.model.AbstractCRUDFacade;
import org.socialmusicdiscovery.server.business.model.core.ReleaseEntity;
import org.socialmusicdiscovery.server.business.repository.core.ReleaseRepository;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

/**
 * Provides functionality to create, update, delete and find a specific release
 */
@Path("/releases")
public class ReleaseFacade extends AbstractCRUDFacade<ReleaseEntity, ReleaseRepository> {
    /**
     * Search for releases matching specified search criteria
     *
     * @param name         Exact name of the release
     * @param nameContains The name of the release has to contain this string
     * @param artist       Identity of an artist that contributes to the release
     * @param work         Identity of an work which is part of the release
     * @return List of matching releases
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ReleaseEntity> search(@QueryParam("name") String name, @QueryParam("nameContains") String nameContains, @QueryParam("artist") String artist, @QueryParam("work") String work) {
        if (name != null) {
            return new CopyHelper().detachedCopy(repository.findByNameWithRelations(name, Arrays.asList("reference"), null));
        } else if (nameContains != null) {
            return new CopyHelper().detachedCopy(repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null));
        } else if (artist != null) {
            return new CopyHelper().detachedCopy(repository.findByArtistWithRelations(artist, Arrays.asList("reference"), null));
        } else if (work != null) {
            return new CopyHelper().detachedCopy(repository.findByWorkWithRelations(work, Arrays.asList("reference"), null));
        } else {
            return new CopyHelper().detachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null));
        }
    }

    /**
     * Get information about a specific release
     *
     * @param id Identity of the release
     * @return Information about the release
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public ReleaseEntity get(@PathParam("id") String id) {
        return super.getEntity(id);
    }

    /**
     * Create a new release
     *
     * @param release Information about the release to create
     * @return Information about newly created release including its generated identity
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReleaseEntity create(ReleaseEntity release) {
        return super.createEntity(release);
    }

    /**
     * Update an existing release
     *
     * @param id      Identity of the release
     * @param release Updated information about the release
     * @return Updated information about the release
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public ReleaseEntity update(@PathParam("id") String id, ReleaseEntity release) {
        return super.updateEntity(id, release);
    }

    /**
     * Delete an existing release
     *
     * @param id Identity of the release
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public void delete(@PathParam("id") String id) {
        super.deleteEntity(id);
    }
}
