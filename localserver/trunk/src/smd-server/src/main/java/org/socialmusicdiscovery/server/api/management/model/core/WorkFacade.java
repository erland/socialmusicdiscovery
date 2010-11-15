package org.socialmusicdiscovery.server.api.management.model.core;

import org.socialmusicdiscovery.server.api.management.model.BaseCRUDFacade;
import org.socialmusicdiscovery.server.business.logic.DetachHelper;
import org.socialmusicdiscovery.server.business.model.core.Work;
import org.socialmusicdiscovery.server.business.repository.core.WorkRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

@Path("/works")
public class WorkFacade extends BaseCRUDFacade<Work, WorkRepository> {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Work> search(@QueryParam("name") String name, @QueryParam("nameContains") String nameContains, @QueryParam("release") String release, @QueryParam("artist") String artist) {
        if (name != null) {
            return DetachHelper.createDetachedCopy(repository.findByNameWithRelations(name, Arrays.asList("reference"), null));
        } else if (nameContains != null) {
            return DetachHelper.createDetachedCopy(repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null));
        } else if (release != null) {
            return DetachHelper.createDetachedCopy(repository.findByReleaseWithRelations(release, Arrays.asList("reference"), null));
        } else if (artist != null) {
            return DetachHelper.createDetachedCopy(repository.findByArtistWithRelations(artist, Arrays.asList("reference"), null));
        } else {
            return DetachHelper.createDetachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null));
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public Work get(@PathParam("id") String id) {
        return super.get(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Work create(Work work) {
        return super.create(work);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public Work update(@PathParam("id") String id, Work work) {
        return super.update(id, work);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public void delete(@PathParam("id") String id) {
        super.delete(id);
    }
}
