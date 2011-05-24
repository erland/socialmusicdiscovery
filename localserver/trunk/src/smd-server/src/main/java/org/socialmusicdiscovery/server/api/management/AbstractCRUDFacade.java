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

/**
 * Abstract base class to provided basic functionality for all entities that want to provide a create, read, update, delete interface
 * @param <K> The primary key class used by the entity
 * @param <E> The class of the entity which this facade manages
 * @param <R> The interface class of the repository which manage the entity this facade provides an interface for
 */
public abstract class AbstractCRUDFacade<K, E, R extends EntityRepository<K, E>> {
    /** The repository used by this facade */
    @Inject
    protected R repository;

    /** The entity manager instance used by this facade */
    @Inject
    private EntityManager em;

    /** The transaction manager used by this facade */
    @Inject
    protected TransactionManager transactionManager;

    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Get the repository instance
     * @return The repository instance
     */
    protected R getRepository() {
        return repository;
    }

    /**
     * Create a new instance and inject all member variables annotated with a {@link @Inject} annotation
     */
    public AbstractCRUDFacade() {
        InjectHelper.injectMembers(this);
    }

    /**
     * Get the entity with the specified identity
     * @param id The identity of the entity
     * @return The found instance or null if it doesn't exist
     */
    protected E getEntity(K id) {
        return repository.findById(id);
    }

    /**
     * Creates a new entity instance
     * @param entity The data for the instance which should be created
     * @return The persistent newly created instance
     */
    protected E createEntity(E entity) {
        repository.create(entity);
        return entity;
    }

    /**
     * Updates the specified entity instance
     * @param entity The data for the instance which should be updated
     * @return The persistent updated instance
     */
    protected E updateEntity(E entity) {
        entity = repository.merge(entity);
        return entity;
    }

    /**
     * Deletes the entity instance with the specified identity
     * @param id The primary key of the entity instance to delete
     */
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
