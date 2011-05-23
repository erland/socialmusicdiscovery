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

package org.socialmusicdiscovery.server.business.repository;

import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;

import javax.persistence.EntityManager;

/**
 * Abstract repository class for all entity repository classes which inherits from {@link AbstractSMDIdentityEntity}
 * @param <E> The type of the entity which this repositories manages
 */
public abstract class AbstractJPASMDIdentityRepository<E extends AbstractSMDIdentityEntity> extends AbstractJPAEntityRepository<String, E> implements SMDIdentityRepository<E> {
    public AbstractJPASMDIdentityRepository(EntityManager em) {
        super(em);
    }

    /**
     * Persists the specified entity and automatically call {@link AbstractSMDIdentityEntity#setReference(org.socialmusicdiscovery.server.business.model.SMDIdentityReference)}
     * to set its reference unless it already have been set.
     * @param entity The entity to persist
     */
	public void create(E entity) {
        if(entity.getReference()==null || entity.getReference().getId() == null) {
            entity.setReference(SMDIdentityReferenceEntity.forEntity(entity));
        }
        super.create(entity);
    }

    /**
     * Merge the specified entity with a previously persisted instance and automatically call
     * {@link AbstractSMDIdentityEntity#setReference(org.socialmusicdiscovery.server.business.model.SMDIdentityReference)}
     * to set its reference unless it already have been set.
     * @param entity The entity to persist
     * @return The merged instance which are persisted
     */
    public E merge(E entity) {
        if(entity.getReference()==null || entity.getReference().getId() == null) {
            entity.setReference(SMDIdentityReferenceEntity.forEntity(entity));
        }
        return super.merge(entity);
    }
}