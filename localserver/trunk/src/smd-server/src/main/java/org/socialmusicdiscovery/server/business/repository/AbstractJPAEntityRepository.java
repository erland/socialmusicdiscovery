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
        return query.getResultList();
    }
}