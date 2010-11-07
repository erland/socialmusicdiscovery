package org.socialmusicdiscovery.server.api.management.model.core;

import org.socialmusicdiscovery.server.api.management.model.BaseCRUDFacade;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.repository.core.ReleaseRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

@Path("/releases")
public class ReleaseFacade extends BaseCRUDFacade<Release, ReleaseRepository> {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Release> search(@QueryParam("name") String name, @QueryParam("nameContains") String nameContains, @QueryParam("artist") String artist, @QueryParam("work") String work) {
        if(name != null) {
            return repository.findByNameWithRelations(name,Arrays.asList("reference"), null);
        }else if(nameContains != null) {
            return repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null);
        }else if(artist != null) {
            return repository.findByArtistWithRelations(artist,Arrays.asList("reference"), null);
        }else if(work != null) {
            return repository.findByWorkWithRelations(work,Arrays.asList("reference"), null);
        }else {
            return repository.findAllWithRelations(Arrays.asList("reference"), null);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public Release get(@PathParam("id") String id) {
        return super.get(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Release create(Release release) {
        return super.create(release);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public Release update(@PathParam("id") String id, Release release) {
        return super.update(id,release);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public void delete(@PathParam("id") String id) {
        super.delete(id);
    }
}
