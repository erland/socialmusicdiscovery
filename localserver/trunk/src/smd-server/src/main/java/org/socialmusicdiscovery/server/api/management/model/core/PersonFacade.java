package org.socialmusicdiscovery.server.api.management.model.core;

import org.socialmusicdiscovery.server.api.management.model.AbstractCRUDFacade;
import org.socialmusicdiscovery.server.business.model.core.PersonEntity;
import org.socialmusicdiscovery.server.business.repository.core.PersonRepository;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

/**
 * Provides functionality to create, update, delete and find a specific person
 */
@Path("/persons")
public class PersonFacade extends AbstractCRUDFacade<PersonEntity, PersonRepository> {
    /**
     * Search for persons matching the search criterias
     *
     * @param name         Exact name of person
     * @param nameContains The name of the person has to contain this string
     * @return List of matching persons
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<PersonEntity> search(@QueryParam("name") String name, @QueryParam("nameContains") String nameContains) {
        if (name != null) {
            return CopyHelper.createDetachedCopy(repository.findByNameWithRelations(name, Arrays.asList("reference"), null));
        } else if (nameContains != null) {
            return CopyHelper.createDetachedCopy(repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null));
        } else {
            return CopyHelper.createDetachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null));
        }
    }

    /**
     * Get information about a specific person
     *
     * @param id Identity of the person
     * @return Information about the person
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public PersonEntity get(@PathParam("id") String id) {
        return super.getEntity(id);
    }

    /**
     * Create a new person
     *
     * @param person Information about the person to create
     * @return Information about newly created person including its generated identity
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PersonEntity create(PersonEntity person) {
        return super.createEntity(person);
    }

    /**
     * Update an existing person
     *
     * @param id     Identity of person
     * @param person Updated information about the person
     * @return Updated information about the person
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public PersonEntity update(@PathParam("id") String id, PersonEntity person) {
        return super.updateEntity(id, person);
    }

    /**
     * Delete an existing person
     *
     * @param id Identity of person
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public void delete(@PathParam("id") String id) {
        super.deleteEntity(id);
    }
}
