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
import org.socialmusicdiscovery.server.business.model.core.ContributorEntity;
import org.socialmusicdiscovery.server.business.repository.core.ContributorRepository;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * Provides functionality to create, update, delete and find a specific recording
 */
@Path("/contributors")
public class ContributorFacade extends AbstractSMDIdentityCRUDFacade<ContributorEntity, ContributorRepository> {
    @Inject
    private TransactionManager transactionManager;
    /**
     * Search for contributors matching specified search criterias
     *
     * @param release      The identity of the release which the contributor is related to
     * @param work      The identity of the work which the contributor is related to
     * @param recording      The identity of the recording which the contributor is related to
     * @param recordingSession      The identity of the recording session which the contributor is related to
     * @param artist     The identity of the artist which the contributor is related to
     * @return List of matching recordings
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ContributorEntity> search(@QueryParam("release") String release, @QueryParam("work") String work, @QueryParam("recording") String recording, @QueryParam("recordingSession") String recordingSession, @QueryParam("artist") String artist) {
        try {
            transactionManager.begin();
            if(release!=null) {
                return new CopyHelper().detachedCopy(repository.findByReleaseWithRelations(release, Arrays.asList("reference"), null), Expose.class);
            }else if (work != null) {
                return new CopyHelper().detachedCopy(repository.findByWorkWithRelations(work, Arrays.asList("reference"), null), Expose.class);
            }else if (recording != null) {
                return new CopyHelper().detachedCopy(repository.findByRecordingWithRelations(recording, Arrays.asList("reference"), null), Expose.class);
            }else if (recordingSession != null) {
                return new CopyHelper().detachedCopy(repository.findByRecordingSessionWithRelations(recordingSession, Arrays.asList("reference"), null), Expose.class);
            }else if (artist!=null) {
                return new CopyHelper().detachedCopy(repository.findByArtistWithRelations(release, Arrays.asList("reference"), null), Expose.class);
            } else {
                return new CopyHelper().detachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null), Expose.class);
            }
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Get information about a specific contributor
     *
     * @param id Identity of contributor
     * @return Information about contributor
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public ContributorEntity get(@PathParam("id") String id) {
        try {
            transactionManager.begin();
            return new CopyHelper().copy(super.getEntity(id), Expose.class);
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Create a new contributor
     *
     * @param contributor Information about contributor to create
     * @return Information about newly created contributor including its generated identity
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ContributorEntity create(ContributorEntity contributor) {
        try {
            transactionManager.begin();
            contributor.setLastUpdated(new Date());
            contributor.setLastUpdatedBy(super.CHANGED_BY);
            return new CopyHelper().copy(super.createEntity(contributor), Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Update an existing contributor
     *
     * @param id        Identity of contributor
     * @param contributor Information about contributor
     * @return Information about contributor
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public ContributorEntity update(@PathParam("id") String id, ContributorEntity contributor) {
        try {
            transactionManager.begin();
            contributor.setLastUpdated(new Date());
            contributor.setLastUpdatedBy(super.CHANGED_BY);
            return new CopyHelper().copy(super.updateEntity(id, contributor), Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Delete an existing contributor
     *
     * @param id Identity of contributor
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
