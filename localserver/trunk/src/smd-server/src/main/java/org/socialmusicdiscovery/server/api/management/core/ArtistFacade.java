package org.socialmusicdiscovery.server.api.management.core;

import org.socialmusicdiscovery.server.api.management.BaseCRUDFacade;
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
    public Collection<Artist> search(@QueryParam("name") String name) {
        if(name != null) {
            return repository.findByNameWithRelations(name, Arrays.asList("reference"), null);
        }else {
            return repository.findAllWithRelations(Arrays.asList("reference"), null);
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
