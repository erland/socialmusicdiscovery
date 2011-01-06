package org.socialmusicdiscovery.server.api.management.model.core;

import org.socialmusicdiscovery.server.api.management.model.AbstractCRUDFacade;
import org.socialmusicdiscovery.server.business.model.core.LabelEntity;
import org.socialmusicdiscovery.server.business.repository.core.LabelRepository;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

/**
 * Provides functionality to create, update, delete and find a specific label
 */
@Path("/labels")
public class LabelFacade extends AbstractCRUDFacade<LabelEntity, LabelRepository> {
    /**
     * Search for matching labels
     *
     * @param name         Exact name of label
     * @param nameContains The name of the label has to contain this string
     * @return List of matching labels
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<LabelEntity> search(@QueryParam("name") String name, @QueryParam("nameContains") String nameContains) {
        if (name != null) {
            return new CopyHelper().detachedCopy(repository.findByNameWithRelations(name, Arrays.asList("reference"), null));
        } else if (nameContains != null) {
            return new CopyHelper().detachedCopy(repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null));
        } else {
            return new CopyHelper().detachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null));
        }
    }

    /**
     * Get information about the label with the specified identity
     *
     * @param id Identity of the label
     * @return Information about the label
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public LabelEntity get(@PathParam("id") String id) {
        return super.getEntity(id);
    }

    /**
     * Creates a new label
     *
     * @param label Information about the label to create
     * @return Information about the newly create label including its generated identity
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public LabelEntity create(LabelEntity label) {
        return super.createEntity(label);
    }

    /**
     * Update an existing label
     *
     * @param id    Identity of the label
     * @param label Updated information about the label
     * @return The updated information about the label
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public LabelEntity update(@PathParam("id") String id, LabelEntity label) {
        return super.updateEntity(id, label);
    }

    /**
     * Delete an existing label
     *
     * @param id Identity of the label
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public void delete(@PathParam("id") String id) {
        super.deleteEntity(id);
    }
}
