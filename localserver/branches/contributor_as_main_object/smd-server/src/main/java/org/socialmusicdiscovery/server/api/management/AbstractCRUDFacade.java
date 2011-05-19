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

package org.socialmusicdiscovery.server.api.management;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.TransactionManager;
import org.socialmusicdiscovery.server.business.repository.EntityRepository;

import javax.persistence.EntityManager;

public abstract class AbstractCRUDFacade<K, E, R extends EntityRepository<K, E>> {
    @Inject
    protected R repository;

    @Inject
    private EntityManager em;

    @Inject
    private TransactionManager transactionManager;

    protected EntityManager getEntityManager() {
        return em;
    }

    protected R getRepository() {
        return repository;
    }

    public AbstractCRUDFacade() {
        InjectHelper.injectMembers(this);
    }

    protected E getEntity(K id) {
        return repository.findById(id);
    }

    protected E createEntity(E entity) {
        repository.create(entity);
        return entity;
    }

    protected E updateEntity(E entity) {
        entity = repository.merge(entity);
        return entity;
    }

    protected void deleteEntity(K id) {
        if (id != null) {
            E entity = repository.findById(id);
            if (entity != null) {
                repository.remove(entity);
            } else {
                transactionManager.setRollbackOnly();
            }
        }
    }
}
