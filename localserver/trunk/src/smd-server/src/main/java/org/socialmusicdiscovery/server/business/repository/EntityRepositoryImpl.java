package org.socialmusicdiscovery.server.business.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public abstract class EntityRepositoryImpl<K, E> implements EntityRepository<K, E> {
	protected Class<E> entityClass;

	@PersistenceContext
	protected EntityManager entityManager;

	public EntityRepositoryImpl() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        if(genericSuperclass.getActualTypeArguments().length>1) {
		    this.entityClass = (Class<E>) genericSuperclass.getActualTypeArguments()[1];
        }else {
            this.entityClass = (Class<E>) genericSuperclass.getActualTypeArguments()[0];
        }
	}
    public EntityRepositoryImpl(EntityManager em) {
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
}