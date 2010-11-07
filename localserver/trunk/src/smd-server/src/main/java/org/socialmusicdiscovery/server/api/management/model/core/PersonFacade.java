package org.socialmusicdiscovery.server.api.management.model.core;

import org.socialmusicdiscovery.server.api.management.model.BaseCRUDFacade;
import org.socialmusicdiscovery.server.business.model.core.Person;
import org.socialmusicdiscovery.server.business.repository.core.PersonRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

@Path("/persons")
public class PersonFacade extends BaseCRUDFacade<Person,PersonRepository> {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Person> search(@QueryParam("name") String name, @QueryParam("nameContains") String nameContains) {
        if(name != null) {
            return repository.findByNameWithRelations(name,Arrays.asList("reference"), null);
        }else if(nameContains != null) {
            return repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null);
        }else {
            return repository.findAllWithRelations(Arrays.asList("reference"), null);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public Person get(@PathParam("id") String id) {
        return super.get(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Person create(Person person) {
        return super.create(person);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public Person update(@PathParam("id") String id, Person person) {
        return super.update(id,person);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Override
    public void delete(@PathParam("id") String id) {
        super.delete(id);
    }
}
