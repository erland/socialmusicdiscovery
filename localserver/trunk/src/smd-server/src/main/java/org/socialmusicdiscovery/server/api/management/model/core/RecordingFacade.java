package org.socialmusicdiscovery.server.api.management.model.core;

import org.socialmusicdiscovery.server.api.management.model.BaseCRUDFacade;
import org.socialmusicdiscovery.server.business.logic.DetachHelper;
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.socialmusicdiscovery.server.business.repository.core.RecordingRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

@Path("/recordings")
public class RecordingFacade extends BaseCRUDFacade<Recording, RecordingRepository> {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Recording> search(@QueryParam("name") String name, @QueryParam("nameContains") String nameContains) {
        if (name != null) {
            return DetachHelper.createDetachedCopy(repository.findByNameWithRelations(name, Arrays.asList("reference"), null));
        } else if (nameContains != null) {
            return DetachHelper.createDetachedCopy(repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null));
        } else {
            return DetachHelper.createDetachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null));
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public Recording get(@PathParam("id") String id) {
        return super.get(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Recording create(Recording recording) {
        return super.create(recording);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public Recording update(@PathParam("id") String id, Recording recording) {
        return super.update(id, recording);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public void delete(@PathParam("id") String id) {
        super.delete(id);
    }
}
