package org.socialmusicdiscovery.server.api.management.model;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.TransactionManager;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.repository.EntityRepository;

import javax.persistence.EntityManager;

public abstract class AbstractCRUDFacade<E extends SMDIdentity, R extends EntityRepository<String, E>> {
    @Inject
    protected R repository;

    @Inject
    private EntityManager em;

    @Inject
    private TransactionManager transactionManager;

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
        repository.create(entity);
        return entity;
    }

    protected E updateEntity(String id, E entity) {
        if (id != null && !entity.getId().equals(id)) {
            entity.setId(id);
        }
        entity = repository.merge(entity);
        return entity;
    }

    protected void deleteEntity(String id) {
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
