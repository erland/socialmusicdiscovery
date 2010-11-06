package org.socialmusicdiscovery.server.api.management.model.core;

import org.socialmusicdiscovery.server.api.management.model.BaseCRUDFacade;
import org.socialmusicdiscovery.server.business.model.core.Label;
import org.socialmusicdiscovery.server.business.repository.core.LabelRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

@Path("/labels")
public class LabelFacade extends BaseCRUDFacade<Label,LabelRepository> {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Label> search(@QueryParam("name") String name) {
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
    public Label get(@PathParam("id") String id) {
        return super.get(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Label create(Label label) {
        return super.create(label);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public Label update(@PathParam("id") String id, Label label) {
        return super.update(id,label);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public void delete(@PathParam("id") String id) {
        super.delete(id);
    }
}
