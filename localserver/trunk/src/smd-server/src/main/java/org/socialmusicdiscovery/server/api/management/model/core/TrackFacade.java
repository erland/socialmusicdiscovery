package org.socialmusicdiscovery.server.api.management.model.core;

import com.google.gson.annotations.Expose;
import com.google.inject.Inject;
import org.socialmusicdiscovery.server.api.management.model.AbstractCRUDFacade;
import org.socialmusicdiscovery.server.business.logic.TransactionManager;
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
    @Inject
    private TransactionManager transactionManager;
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
            return new CopyHelper().detachedCopy(repository.findByNameWithRelations(name, Arrays.asList("reference"), Arrays.asList("medium")),Expose.class);
        } else if (nameContains != null) {
            return new CopyHelper().detachedCopy(repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), Arrays.asList("medium")),Expose.class);
        } else if (release != null) {
            return new CopyHelper().detachedCopy(repository.findByReleaseWithRelations(release, Arrays.asList("reference","recording","recording.works"), Arrays.asList("medium")),Expose.class);
        } else if (artist != null) {
            return new CopyHelper().detachedCopy(repository.findByArtistWithRelations(artist, Arrays.asList("reference","recording","recording.works"), Arrays.asList("medium")),Expose.class);
        } else if (work != null) {
            return new CopyHelper().detachedCopy(repository.findByWorkWithRelations(work, Arrays.asList("reference"), Arrays.asList("medium")),Expose.class);
        } else {
            return new CopyHelper().detachedCopy(repository.findAllWithRelations(Arrays.asList("reference","recording","recording.works"), Arrays.asList("medium")),Expose.class);
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
        return new CopyHelper().copy(super.getEntity(id), Expose.class);
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
        try {
            transactionManager.begin();
            return new CopyHelper().copy(super.createEntity(track), Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
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
        try {
            transactionManager.begin();
            return new CopyHelper().copy(super.updateEntity(id, track), Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
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
