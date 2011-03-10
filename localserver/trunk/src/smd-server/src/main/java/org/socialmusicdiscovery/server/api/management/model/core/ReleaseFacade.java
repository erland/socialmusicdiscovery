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
import org.socialmusicdiscovery.server.api.management.model.AbstractCRUDFacade;
import org.socialmusicdiscovery.server.business.logic.TransactionManager;
import org.socialmusicdiscovery.server.business.model.core.ReleaseEntity;
import org.socialmusicdiscovery.server.business.repository.core.ReleaseRepository;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * Provides functionality to create, update, delete and find a specific release
 */
@Path("/releases")
public class ReleaseFacade extends AbstractCRUDFacade<ReleaseEntity, ReleaseRepository> {
    @Inject
    private TransactionManager transactionManager;

    /**
     * Search for releases matching specified search criteria
     *
     * @param name         Exact name of the release
     * @param nameContains The name of the release has to contain this string
     * @param artist       Identity of an artist that contributes to the release
     * @param work         Identity of an work which is part of the release
     * @return List of matching releases
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ReleaseEntity> search(@QueryParam("name") String name, @QueryParam("nameContains") String nameContains, @QueryParam("artist") String artist, @QueryParam("work") String work) {
        if (name != null) {
            return new CopyHelper().detachedCopy(repository.findByNameWithRelations(name, Arrays.asList("reference"), null), Expose.class);
        } else if (nameContains != null) {
            return new CopyHelper().detachedCopy(repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null), Expose.class);
        } else if (artist != null) {
            return new CopyHelper().detachedCopy(repository.findByArtistWithRelations(artist, Arrays.asList("reference"), null), Expose.class);
        } else if (work != null) {
            return new CopyHelper().detachedCopy(repository.findByWorkWithRelations(work, Arrays.asList("reference"), null), Expose.class);
        } else {
            return new CopyHelper().detachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null), Expose.class);
        }
    }

    /**
     * Get information about a specific release
     *
     * @param id Identity of the release
     * @return Information about the release
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public ReleaseEntity get(@PathParam("id") String id) {
        return new CopyHelper().copy(super.getEntity(id), Expose.class);
    }

    /**
     * Create a new release
     *
     * @param release Information about the release to create
     * @return Information about newly created release including its generated identity
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ReleaseEntity create(ReleaseEntity release) {
        try {
            transactionManager.begin();
            release.setLastUpdated(new Date());
            release.setLastUpdatedBy(super.CHANGED_BY);
            return new CopyHelper().copy(super.createEntity(release), Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Update an existing release
     *
     * @param id      Identity of the release
     * @param release Updated information about the release
     * @return Updated information about the release
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public ReleaseEntity update(@PathParam("id") String id, ReleaseEntity release) {
        try {
            transactionManager.begin();
            release.setLastUpdated(new Date());
            release.setLastUpdatedBy(super.CHANGED_BY);
            return new CopyHelper().copy(super.updateEntity(id, release), Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Delete an existing release
     *
     * @param id Identity of the release
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
