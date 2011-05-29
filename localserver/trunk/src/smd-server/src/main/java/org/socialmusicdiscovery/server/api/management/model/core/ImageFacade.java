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
import org.socialmusicdiscovery.server.business.model.core.ImageEntity;
import org.socialmusicdiscovery.server.business.repository.core.ImageRepository;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Date;

/**
 * Provides functionality to create, update, delete and find a specific artist
 */
@Path("/images")
public class ImageFacade extends AbstractSMDIdentityCRUDFacade<ImageEntity, ImageRepository> {

	org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ImageFacade.class);

	/**
     * Search for images matching the specified search criterias
     *
     * @param relatedTo    SMD Identifier of object we're looking image for
     * @param imageType    Filter result to a given type of image
     * @return List of matching images
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ImageEntity> search(@QueryParam("relatedTo") String relatedToSMDId, @QueryParam("imageType") String imageType) {
    	logger.debug("ImageFacade 'search' called!");
    	try {
            transactionManager.begin();
            if ( (relatedToSMDId) != null &&  (imageType == null) ) {
                return new CopyHelper().detachedCopy(repository.findByRelatedToSMDId(relatedToSMDId), Expose.class);
            } else if ( (relatedToSMDId) != null &&  (imageType != null) ) {
                    return new CopyHelper().detachedCopy(repository.findByRelatedToSMDIdAndType(relatedToSMDId, imageType), Expose.class);
            } else if ( (imageType == null) && (relatedToSMDId == null)) {
            	return new CopyHelper().detachedCopy(repository.findAll(), Expose.class);
            } else {
                return null;
            }
        }finally {
            transactionManager.end();
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
    public ImageEntity get(@PathParam("id") String id) {
        try {
            transactionManager.begin();
            return new CopyHelper().copy(super.getEntity(id), Expose.class);
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Creates a new image
     *
     * @param artist Information about the artist which should be created
     * @return The newly created artist including its generated identity
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ImageEntity create(ImageEntity image) {
    	logger.debug("ImageFacade 'create' called!");
    	if(image==null) {
    		logger.error("Image is null");
    		return null;
    	}
        try {
            transactionManager.begin();
            image.setLastUpdated(new Date());
            image.setLastUpdatedBy(super.CHANGED_BY);
            return new CopyHelper().copy(super.createEntity(image), Expose.class);
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
    public ImageEntity update(@PathParam("id") String id, ImageEntity artist) {
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
