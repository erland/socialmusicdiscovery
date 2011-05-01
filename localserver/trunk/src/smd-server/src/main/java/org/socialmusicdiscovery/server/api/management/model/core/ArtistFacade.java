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
import org.socialmusicdiscovery.server.business.model.core.ArtistEntity;
import org.socialmusicdiscovery.server.business.repository.core.ArtistRepository;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * Provides functionality to create, update, delete and find a specific artist
 */
@Path("/artists")
public class ArtistFacade extends AbstractSMDIdentityCRUDFacade<ArtistEntity, ArtistRepository> {
    @Inject
    private TransactionManager transactionManager;
    /**
     * Search for artists matching the specified search criterias
     *
     * @param name         Exact name of artist
     * @param nameContains The name of th artist contains this string
     * @param release      The identity of the release which the artist was a contributor on
     * @param work         The identity of the work which the artist was a contributor on
     * @return List of matching artists
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ArtistEntity> search(@QueryParam("name") String name, @QueryParam("nameContains") String nameContains, @QueryParam("release") String release, @QueryParam("work") String work) {
        if (name != null) {
            return new CopyHelper().detachedCopy(repository.findByNameWithRelations(name, Arrays.asList("reference"), null), Expose.class);
        } else if (nameContains != null) {
            return new CopyHelper().detachedCopy(repository.findByPartialNameWithRelations(nameContains, Arrays.asList("reference"), null), Expose.class);
        } else if (release != null) {
            return new CopyHelper().detachedCopy(repository.findByReleaseWithRelations(release, Arrays.asList("reference"), null), Expose.class);
        } else if (work != null) {
            return new CopyHelper().detachedCopy(repository.findByWorkWithRelations(work, Arrays.asList("reference"), null), Expose.class);
        } else {
            return new CopyHelper().detachedCopy(repository.findAllWithRelations(Arrays.asList("reference"), null), Expose.class);
        }
    }

    /**
     * Get the artist with specified identity
     *
     * @param id The identity of the artist
     * @return Information about the artist
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public ArtistEntity get(@PathParam("id") String id) {
        return new CopyHelper().copy(super.getEntity(id), Expose.class);
    }

    /**
     * Creates a new artist
     *
     * @param artist Information about the artist which should be created
     * @return The newly created artist including its generated identity
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ArtistEntity create(ArtistEntity artist) {
        try {
            transactionManager.begin();
            artist.setLastUpdated(new Date());
            artist.setLastUpdatedBy(super.CHANGED_BY);
            return new CopyHelper().copy(super.createEntity(artist), Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Update an existing artist
     *
     * @param id     The identity of the artist
     * @param artist Information which the artist should be updated with
     * @return The updated artist information
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public ArtistEntity update(@PathParam("id") String id, ArtistEntity artist) {
        try {
            transactionManager.begin();
            artist.setLastUpdated(new Date());
            artist.setLastUpdatedBy(super.CHANGED_BY);
            return new CopyHelper().copy(super.updateEntity(id, artist), Expose.class);
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Deletes an existing artist
     *
     * @param id The identity fo the artist
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
