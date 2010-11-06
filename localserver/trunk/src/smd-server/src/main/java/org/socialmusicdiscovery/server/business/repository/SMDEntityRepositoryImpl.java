package org.socialmusicdiscovery.server.business.repository;

import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.SMDEntityReference;

import javax.persistence.EntityManager;

public abstract class SMDEntityRepositoryImpl<E extends SMDEntity> extends EntityRepositoryImpl<String, E> implements SMDEntityRepository<E> {

	public SMDEntityRepositoryImpl() {}
    public SMDEntityRepositoryImpl(EntityManager em) {
        super(em);
    }

	public void create(E entity) {
        if(entity.getReference()==null || entity.getReference().getId() == null) {
            entity.setReference(SMDEntityReference.forEntity(entity));
            entityManager.persist(entity.getReference());
        }
        super.create(entity);
    }

    public E merge(E entity) {
        if(entity.getReference()==null || entity.getReference().getId() == null) {
            entity.setReference(SMDEntityReference.forEntity(entity));
            entityManager.merge(entity.getReference());
        }
        return super.merge(entity);
    }

	public void remove(E entity) {
        SMDEntityReference reference = entity.getReference();
        super.remove(entity);
        entityManager.remove(reference);
    }
}