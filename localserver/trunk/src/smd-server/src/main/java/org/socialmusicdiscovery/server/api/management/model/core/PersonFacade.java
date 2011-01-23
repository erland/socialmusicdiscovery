package org.socialmusicdiscovery.server.api.management.model.core;

import com.google.gson.annotations.Expose;
import com.google.inject.Inject;
import org.socialmusicdiscovery.server.api.management.model.AbstractCRUDFacade;
import org.socialmusicdiscovery.server.business.logic.TransactionManager;
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
    @Inject
    private TransactionManager transactionManager;
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
            return new CopyHelper().detachedCopy(repository.findByNameWithRelations(name, Arrays.asList("reference"), null), Expose.class);
        } else if (nameContains != null) {
            return new CopyHelper().detachedCopy(repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null), Expose.class);
        } else {
            return new CopyHelper().detachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null), Expose.class);
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
        return new CopyHelper().copy(super.getEntity(id), Expose.class);
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
        try {
            transactionManager.begin();
            return new CopyHelper().copy(super.createEntity(person), Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
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
        try {
            transactionManager.begin();
            return new CopyHelper().copy(super.updateEntity(id, person), Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
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
