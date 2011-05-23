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

import java.util.Collection;

/**
 * Repository that manage the specified type of entity
 * @param <K> The type of the primary key of the entity it manages
 * @param <E> The entity class of the entity it manages
 */
public interface EntityRepository<K, E> {
    /**
     * Persist entity which hasn't been previously persisted
     * @param entity The entity to persist
     */
    void create(E entity);

    /**
     * Remove entity which have been previously persisted
     * @param entity The entity to remove
     */
    void remove(E entity);

    /**
     * Merge entity with a previously persisted version
     * @param entity The entity to merge with the persistent version
     * @return The merged entity which is persisted
     */
    E merge(E entity);

    /**
     * Find the entity instance with the specified primary key
     * @param id The primary key of the entity instance
     * @return The entity instance with the specified identity
     */
    E findById(K id);

    /**
     * Find all entities managed by this repository
     * @return All entity instances managed by this repository
     */
    Collection<E> findAll();

    /**
     * Find all entities managed with this repository which have the specified mandatory relations.
     * @param mandatoryRelations Mandatory relations which the entity needs to have. If this isn't specified, the returned list will be the same as the {@link #findAll()} method
     * @param optionalRelations Optional relations which should be retrieved in the same call
     * @return All matching entity instances
     */
    Collection<E> findAllWithRelations(Collection<String> mandatoryRelations, Collection<String> optionalRelations);
}
