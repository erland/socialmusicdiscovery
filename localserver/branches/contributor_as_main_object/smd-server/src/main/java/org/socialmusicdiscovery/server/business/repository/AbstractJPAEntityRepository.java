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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public abstract class AbstractJPAEntityRepository<K, E> implements EntityRepository<K, E> {
	protected Class<E> entityClass;

	@PersistenceContext
	protected EntityManager entityManager;

	public AbstractJPAEntityRepository() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        if(genericSuperclass.getActualTypeArguments().length>1) {
		    this.entityClass = (Class<E>) genericSuperclass.getActualTypeArguments()[1];
        }else {
            this.entityClass = (Class<E>) genericSuperclass.getActualTypeArguments()[0];
        }
	}
    public AbstractJPAEntityRepository(EntityManager em) {
        this();
        entityManager = em;
    }

	public void create(E entity) {
        entityManager.persist(entity);
    }

    public E merge(E entity) {
        return entityManager.merge(entity);
    }

    public void remove(E entity) {
        entityManager.remove(entity);
    }

	public E findById(K id) {
        return entityManager.find(entityClass, id);
    }

    public Collection<E> findAll() {
        Query query = entityManager.createQuery("from "+entityClass.getSimpleName());
        if(isCacheable()) {
            query.setHint("org.hibernate.cacheable",true);
        }
        return query.getResultList();
    }

    protected String queryStringFor(String entityName, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        return queryStringFor(entityName, mandatoryRelations, optionalRelations, null);
    }
    protected String recordingQueryStringFor(String entityName, String wantedRelationType, String filteredByRelationType, String wantedSearchRelationName, String filteredBySearchRelationName, Collection<String> mandatoryRelations, Collection<String> optionalRelations, Boolean distinctResult) {
        StringBuffer queryString = new StringBuffer(200);
        queryString.append("select distinct ").append(entityName).append(" from RecordingEntity as recording JOIN recording.").append(wantedRelationType).append("SearchRelations as ").append(wantedSearchRelationName).append(" JOIN ").append(wantedSearchRelationName).append(".").append(wantedRelationType).append(" as e JOIN recording.").append(filteredByRelationType).append("SearchRelations as ").append(filteredBySearchRelationName);
        if(mandatoryRelations != null) {
            for (String relation : mandatoryRelations) {
                queryString.append(" JOIN FETCH ").append(entityName).append(".").append(relation);
            }
        }
        if(optionalRelations != null) {
            for (String relation : optionalRelations) {
                queryString.append(" LEFT JOIN FETCH ").append(entityName).append(".").append(relation);
            }
        }
        return queryString.toString();
    }

    protected String queryStringFor(String entityName, Collection<String> mandatoryRelations, Collection<String> optionalRelations, Boolean distinctResult) {
        StringBuffer queryString = new StringBuffer(200);
        String distinct = "";
        if((mandatoryRelations != null && mandatoryRelations.size()>0) || (optionalRelations != null && optionalRelations.size()>0) || (distinctResult != null && distinctResult)) {
            distinct = "distinct ";
        }
        queryString.append("select ").append(distinct).append(entityName).append(" from ").append(entityClass.getSimpleName()).append(" as e");
        if(mandatoryRelations != null) {
            for (String relation : mandatoryRelations) {
                if(relation.contains(".")) {
                    queryString.append(" JOIN FETCH ").append(relation).append(" as ").append(relation.substring(relation.indexOf(".")+1));
                }else {
                    queryString.append(" JOIN FETCH ").append(entityName).append(".").append(relation).append(" as ").append(relation);
                }
            }
        }
        if(optionalRelations != null) {
            for (String relation : optionalRelations) {
                if(relation.contains(".")) {
                    queryString.append(" LEFT JOIN FETCH ").append(relation).append(" as ").append(relation.substring(relation.indexOf(".")+1));
                }else {
                    queryString.append(" LEFT JOIN FETCH ").append(entityName).append(".").append(relation).append(" as ").append(relation);
                }
            }
        }
        return queryString.toString();
    }
    
    public Collection<E> findAllWithRelations(Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations,optionalRelations));
        if(isCacheable()) {
            query.setHint("org.hibernate.cacheable",true);
        }
        return query.getResultList();
    }

    protected boolean isCacheable() {
        return false;
    }
}