package org.socialmusicdiscovery.server.api.management.model.core;

import org.socialmusicdiscovery.server.api.management.model.AbstractCRUDFacade;
import org.socialmusicdiscovery.server.business.model.core.ArtistEntity;
import org.socialmusicdiscovery.server.business.repository.core.ArtistRepository;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

/**
 * Provides functionality to create, update, delete and find a specific artist
 */
@Path("/artists")
public class ArtistFacade extends AbstractCRUDFacade<ArtistEntity, ArtistRepository> {
    /**
     * Search for artists matching the specified search criterias
     *
     * @param name         Exact name of artist
     * @param nameContains The name of th artist contains this string
     * @param release      The identity of the release which the artist was a contributor on
     * @param work         The identity of the work which the artist was a contributor on
     * @return List of matching artists
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ArtistEntity> search(@QueryParam("name") String name, @QueryParam("nameContains") String nameContains, @QueryParam("release") String release, @QueryParam("work") String work) {
        if (name != null) {
            return new CopyHelper().detachedCopy(repository.findByNameWithRelations(name, Arrays.asList("reference"), null));
        } else if (nameContains != null) {
            return new CopyHelper().detachedCopy(repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null));
        } else if (release != null) {
            return new CopyHelper().detachedCopy(repository.findByReleaseWithRelations(release, Arrays.asList("reference"), null));
        } else if (work != null) {
            return new CopyHelper().detachedCopy(repository.findByWorkWithRelations(work, Arrays.asList("reference"), null));
        } else {
            return new CopyHelper().detachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null));
        }
    }

    /**
     * Get the artist with specified identity
     *
     * @param id The identity of the artist
     * @return Information about the artist
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public ArtistEntity get(@PathParam("id") String id) {
        return super.getEntity(id);
    }

    /**
     * Creates a new artist
     *
     * @param artist Information about the artist which should be created
     * @return The newly created artist including its generated identity
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ArtistEntity create(ArtistEntity artist) {
        return super.createEntity(artist);
    }

    /**
     * Update an existing artist
     *
     * @param id     The identity of the artist
     * @param artist Information which the artist should be updated with
     * @return The updated artist information
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public ArtistEntity update(@PathParam("id") String id, ArtistEntity artist) {
        return super.updateEntity(id, artist);
    }

    /**
     * Deletes an existing artist
     *
     * @param id The identity fo the artist
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public void delete(@PathParam("id") String id) {
        super.deleteEntity(id);
    }
}
