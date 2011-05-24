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

package org.socialmusicdiscovery.server.api.management.model;

import org.socialmusicdiscovery.server.api.management.AbstractCRUDFacade;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.repository.EntityRepository;

/**
 * Abstract base class to provided basic functionality for all entities that want to provide a create, read, update, delete interface and implements
 * the SMDIdentity interface
 * @param <E> The class of the entity which this facade manages
 * @param <R> The interface class of the repository which manage the entity this facade provides an interface for
 */
public abstract class AbstractSMDIdentityCRUDFacade<E extends SMDIdentity, R extends EntityRepository<String, E>>  extends AbstractCRUDFacade<String,E,R> {
    protected final String CHANGED_BY = "smd";

    /**
     * Updates the specified entity instance
     * @param id The identity of the instance to update
     * @param entity The data for the instance which should be updated
     * @return The persistent updated instance
     */
    protected E updateEntity(String id, E entity) {
        if (id != null && !entity.getId().equals(id)) {
            entity.setId(id);
        }
        return super.updateEntity(entity);
    }
}
