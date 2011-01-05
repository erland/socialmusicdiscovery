package org.socialmusicdiscovery.server.api.management.model.core;

import org.socialmusicdiscovery.server.api.management.model.AbstractCRUDFacade;
import org.socialmusicdiscovery.server.business.model.core.TrackEntity;
import org.socialmusicdiscovery.server.business.repository.core.TrackRepository;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

/**
 * Provides functionality to create, update, delete and find a specific track
 */
@Path("/tracks")
public class TrackFacade extends AbstractCRUDFacade<TrackEntity, TrackRepository> {
    /**
     * Search for tracks matching specified search criterias
     *
     * @param name         Exact title of track
     * @param nameContains The title of the track as to contains this string
     * @param release      Identity of the release the track is part of
     * @param artist       Identity of an artist which contributes to the track
     * @param work         Identity of the work the track represents
     * @return List of matching tracks
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<TrackEntity> search(@QueryParam("name") String name, @QueryParam("nameContains") String nameContains, @QueryParam("release") String release, @QueryParam("artist") String artist, @QueryParam("work") String work) {
        if (name != null) {
            return CopyHelper.createDetachedCopy(repository.findByNameWithRelations(name, Arrays.asList("reference"), null));
        } else if (nameContains != null) {
            return CopyHelper.createDetachedCopy(repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null));
        } else if (release != null) {
            return CopyHelper.createDetachedCopy(repository.findByReleaseWithRelations(release, Arrays.asList("reference", "medium"), null));
        } else if (artist != null) {
            return CopyHelper.createDetachedCopy(repository.findByArtistWithRelations(artist, Arrays.asList("reference"), null));
        } else if (work != null) {
            return CopyHelper.createDetachedCopy(repository.findByWorkWithRelations(work, Arrays.asList("reference"), null));
        } else {
            return CopyHelper.createDetachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null));
        }
    }

    /**
     * Get information about a specific track
     *
     * @param id Identity of the track
     * @return Information about the track
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public TrackEntity get(@PathParam("id") String id) {
        return super.getEntity(id);
    }

    /**
     * Create a new track
     *
     * @param track Information about the track to create
     * @return Information about newly created track including its generated identity
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TrackEntity create(TrackEntity track) {
        return super.createEntity(track);
    }

    /**
     * Update an existing track
     *
     * @param id    Identity of the track
     * @param track Updated information about the track
     * @return Updated information about the track
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public TrackEntity update(@PathParam("id") String id, TrackEntity track) {
        return super.updateEntity(id, track);
    }

    /**
     * Delete an existing track
     *
     * @param id Identity of the track
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public void delete(@PathParam("id") String id) {
        super.deleteEntity(id);
    }
}
