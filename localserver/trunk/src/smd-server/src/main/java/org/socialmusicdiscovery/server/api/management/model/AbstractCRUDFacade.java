package org.socialmusicdiscovery.server.api.management.model;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.repository.EntityRepository;

import javax.persistence.EntityManager;

public abstract class AbstractCRUDFacade<E extends SMDIdentity, R extends EntityRepository<String, E>> {
    @Inject
    protected R repository;

    @Inject
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public AbstractCRUDFacade() {
        InjectHelper.injectMembers(this);
    }

    protected E getEntity(String id) {
        return repository.findById(id);
    }

    protected E createEntity(E entity) {
        em.getTransaction().begin();
        repository.create(entity);
        em.getTransaction().commit();
        return entity;
    }

    protected E updateEntity(String id, E entity) {
        em.getTransaction().begin();
        if (id != null && !entity.getId().equals(id)) {
            entity.setId(id);
        }
        entity = repository.merge(entity);
        em.getTransaction().commit();
        return entity;
    }

    protected void deleteEntity(String id) {
        if (id != null) {
            em.getTransaction().begin();
            E artist = repository.findById(id);
            if (artist != null) {
                repository.remove(artist);
                em.getTransaction().commit();
            } else {
                em.getTransaction().rollback();
            }
        }
    }
}
