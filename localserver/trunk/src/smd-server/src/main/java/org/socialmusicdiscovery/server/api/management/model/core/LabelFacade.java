/*
 *  Copyright 2010-2011, Social Music Discovery project
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of Social Music Discovery project nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.socialmusicdiscovery.server.api.management.model.core;

import com.google.gson.annotations.Expose;
import com.google.inject.Inject;
import org.socialmusicdiscovery.server.api.management.model.AbstractSMDIdentityCRUDFacade;
import org.socialmusicdiscovery.server.business.logic.TransactionManager;
import org.socialmusicdiscovery.server.business.model.core.LabelEntity;
import org.socialmusicdiscovery.server.business.repository.core.LabelRepository;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * Provides functionality to create, update, delete and find a specific label
 */
@Path("/labels")
public class LabelFacade extends AbstractSMDIdentityCRUDFacade<LabelEntity, LabelRepository> {
    @Inject
    private TransactionManager transactionManager;
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
        try {
            transactionManager.begin();
            if (name != null) {
                return new CopyHelper().detachedCopy(repository.findByNameWithRelations(name, Arrays.asList("reference"), null), Expose.class);
            } else if (nameContains != null) {
                return new CopyHelper().detachedCopy(repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null), Expose.class);
            } else {
                return new CopyHelper().detachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null), Expose.class);
            }
        }finally {
            transactionManager.end();
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
        try {
            transactionManager.begin();
            return new CopyHelper().copy(super.getEntity(id), Expose.class);
        }finally {
            transactionManager.end();
        }
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
        try {
            transactionManager.begin();
            label.setLastUpdated(new Date());
            label.setLastUpdatedBy(super.CHANGED_BY);
            return new CopyHelper().copy(super.createEntity(label), Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
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
        try {
            transactionManager.begin();
            label.setLastUpdated(new Date());
            label.setLastUpdatedBy(super.CHANGED_BY);
            return new CopyHelper().copy(super.updateEntity(id, label), Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
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
