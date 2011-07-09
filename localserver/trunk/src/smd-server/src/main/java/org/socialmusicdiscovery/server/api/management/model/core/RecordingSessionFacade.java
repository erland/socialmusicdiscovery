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
import org.socialmusicdiscovery.server.business.model.core.RecordingSessionEntity;
import org.socialmusicdiscovery.server.business.repository.core.RecordingSessionRepository;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * Provides functionality to create, update, delete and find a specific recording session
 */
@Path("/recordingsessions")
public class RecordingSessionFacade extends AbstractSMDIdentityCRUDFacade<RecordingSessionEntity, RecordingSessionRepository> {
    /**
     * Search for recordings sessions
     *
     * @return List of recording sessions
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<RecordingSessionEntity> search() {
        try {
            transactionManager.begin();
            return new CopyHelper().detachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null), Expose.class);
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Get information about a specific recording session
     *
     * @param id Identity of recording session
     * @return Information about recording session
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public RecordingSessionEntity get(@PathParam("id") String id) {
        try {
            transactionManager.begin();
            return new CopyHelper().copy(super.getEntity(id), Expose.class);
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Create a new recording session
     *
     * @param recordingSession Information about recording session to create
     * @return Information about newly created recording session including its generated identity
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RecordingSessionEntity create(RecordingSessionEntity recordingSession) {
        try {
            transactionManager.begin();
            recordingSession.setLastUpdated(new Date());
            recordingSession.setLastUpdatedBy(super.CHANGED_BY);
            RecordingSessionEntity createdEntity = super.createEntity(recordingSession);
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
     * Update an existing recording session
     *
     * @param id               Identity of recording session
     * @param recordingSession Information about recording session
     * @return Information about recording session
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public RecordingSessionEntity update(@PathParam("id") String id, RecordingSessionEntity recordingSession) {
        try {
            transactionManager.begin();
            recordingSession.setLastUpdated(new Date());
            recordingSession.setLastUpdatedBy(super.CHANGED_BY);
            RecordingSessionEntity updatedEntity = super.updateEntity(id, recordingSession);
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
     * Delete an existing recording session
     *
     * @param id Identity of recording session
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
