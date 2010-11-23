package org.socialmusicdiscovery.server.api.management.model.core;

import org.socialmusicdiscovery.server.api.management.model.BaseCRUDFacade;
import org.socialmusicdiscovery.server.business.logic.DetachHelper;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.server.business.repository.core.TrackRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

@Path("/tracks")
public class TrackFacade extends BaseCRUDFacade<Track, TrackRepository> {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Track> search(@QueryParam("name") String name, @QueryParam("nameContains") String nameContains, @QueryParam("release") String release, @QueryParam("artist") String artist, @QueryParam("work") String work) {
        if (name != null) {
            return DetachHelper.createDetachedCopy(repository.findByNameWithRelations(name, Arrays.asList("reference"), null));
        } else if (nameContains != null) {
            return DetachHelper.createDetachedCopy(repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null));
        } else if (release != null) {
            return DetachHelper.createDetachedCopy(repository.findByReleaseWithRelations(release, Arrays.asList("reference", "medium"), null));
        } else if (artist != null) {
            return DetachHelper.createDetachedCopy(repository.findByArtistWithRelations(artist, Arrays.asList("reference"), null));
        } else if (work != null) {
            return DetachHelper.createDetachedCopy(repository.findByWorkWithRelations(work, Arrays.asList("reference"), null));
        } else {
            return DetachHelper.createDetachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null));
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public Track get(@PathParam("id") String id) {
        return super.get(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Track create(Track track) {
        return super.create(track);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public Track update(@PathParam("id") String id, Track track) {
        return super.update(id, track);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public void delete(@PathParam("id") String id) {
        super.delete(id);
    }
}
