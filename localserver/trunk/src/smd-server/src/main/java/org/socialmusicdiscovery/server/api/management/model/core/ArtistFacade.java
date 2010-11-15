package org.socialmusicdiscovery.server.api.management.model.core;

import org.socialmusicdiscovery.server.api.management.model.BaseCRUDFacade;
import org.socialmusicdiscovery.server.business.logic.DetachHelper;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.repository.core.ArtistRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

@Path("/artists")
public class ArtistFacade extends BaseCRUDFacade<Artist, ArtistRepository> {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Artist> search(@QueryParam("name") String name, @QueryParam("nameContains") String nameContains, @QueryParam("release") String release, @QueryParam("work") String work) {
        if (name != null) {
            return DetachHelper.createDetachedCopy(repository.findByNameWithRelations(name, Arrays.asList("reference"), null));
        } else if (nameContains != null) {
            return DetachHelper.createDetachedCopy(repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null));
        } else if (release != null) {
            return DetachHelper.createDetachedCopy(repository.findByReleaseWithRelations(release, Arrays.asList("reference"), null));
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
    public Artist get(@PathParam("id") String id) {
        return super.get(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Artist create(Artist artist) {
        return super.create(artist);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public Artist update(@PathParam("id") String id, Artist artist) {
        return super.update(id, artist);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public void delete(@PathParam("id") String id) {
        super.delete(id);
    }
}
