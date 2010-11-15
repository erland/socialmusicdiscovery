package org.socialmusicdiscovery.server.api.management.model;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.repository.EntityRepository;

import javax.persistence.EntityManager;

public abstract class BaseCRUDFacade<T extends SMDEntity, R extends EntityRepository<String, T>> {
    @Inject
    protected R repository;

    @Inject
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public BaseCRUDFacade() {
        InjectHelper.injectMembers(this);
    }

    public T get(String id) {
        return repository.findById(id);
    }

    public T create(T entity) {
        em.getTransaction().begin();
        repository.create(entity);
        em.getTransaction().commit();
        return entity;
    }

    public T update(String id, T entity) {
        em.getTransaction().begin();
        if (id != null && !entity.getId().equals(id)) {
            entity.setId(id);
        }
        entity = repository.merge(entity);
        em.getTransaction().commit();
        return entity;
    }

    public void delete(String id) {
        if (id != null) {
            em.getTransaction().begin();
            T artist = repository.findById(id);
            if (artist != null) {
                repository.remove(artist);
                em.getTransaction().commit();
            } else {
                em.getTransaction().rollback();
            }
        }
    }
}
