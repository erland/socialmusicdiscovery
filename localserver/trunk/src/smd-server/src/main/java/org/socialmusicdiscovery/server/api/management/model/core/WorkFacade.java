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
import org.socialmusicdiscovery.server.api.management.model.AbstractSMDIdentityCRUDFacade;
import org.socialmusicdiscovery.server.business.model.core.WorkEntity;
import org.socialmusicdiscovery.server.business.repository.core.WorkRepository;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * Provides functionality to create, update, delete and find a specific work
 */
@Path("/works")
public class WorkFacade extends AbstractSMDIdentityCRUDFacade<WorkEntity, WorkRepository> {
    /**
     * Search for work matching the search criterias
     *
     * @param name         Exact name of the work
     * @param nameContains The name of the work has to contain this string
     * @param release      Identity of a release which the work is part of
     * @param artist       Identity of an artist which contributes to the work
     * @return List of matching works
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<WorkEntity> search(@QueryParam("name") String name, @QueryParam("nameContains") String nameContains, @QueryParam("release") String release, @QueryParam("artist") String artist) {
        try {
            transactionManager.begin();
            if (name != null) {
                return new CopyHelper().detachedCopy(repository.findByNameWithRelations(name, Arrays.asList("reference"), null), Expose.class);
            } else if (nameContains != null) {
                return new CopyHelper().detachedCopy(repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null), Expose.class);
            } else if (release != null) {
                return new CopyHelper().detachedCopy(repository.findByReleaseWithRelations(release, Arrays.asList("reference"), null), Expose.class);
            } else if (artist != null) {
                return new CopyHelper().detachedCopy(repository.findByArtistWithRelations(artist, Arrays.asList("reference"), null), Expose.class);
            } else {
                return new CopyHelper().detachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null), Expose.class);
            }
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Get information about a specific work
     *
     * @param id Identity of the work
     * @return Information about the work
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public WorkEntity get(@PathParam("id") String id) {
        try {
            transactionManager.begin();
            return new CopyHelper().copy(super.getEntity(id), Expose.class);
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Create a new work
     *
     * @param work Information about work to be created
     * @return Information about newly created work including its generated identity
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkEntity create(WorkEntity work) {
        try {
            transactionManager.begin();
            work.setLastUpdated(new Date());
            work.setLastUpdatedBy(super.CHANGED_BY);
            WorkEntity createdEntity = super.createEntity(work);
            getRepository().refresh(createdEntity);
            return new CopyHelper().copy(createdEntity, Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Update an existing work
     *
     * @param id   Identity of the work
     * @param work Updated information about the work
     * @return Updated information about the work
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public WorkEntity update(@PathParam("id") String id, WorkEntity work) {
        try {
            transactionManager.begin();
            work.setLastUpdated(new Date());
            work.setLastUpdatedBy(super.CHANGED_BY);
            WorkEntity updatedEntity = super.updateEntity(id, work);
            getRepository().refresh(updatedEntity);
            return new CopyHelper().copy(updatedEntity, Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Delete an existing work
     *
     * @param id Identity of the work
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
