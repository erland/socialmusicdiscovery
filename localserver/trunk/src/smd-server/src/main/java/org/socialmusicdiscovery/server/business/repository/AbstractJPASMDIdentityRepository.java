package org.socialmusicdiscovery.server.business.repository;

import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReference;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;

import javax.persistence.EntityManager;

public abstract class AbstractJPASMDIdentityRepository<E extends AbstractSMDIdentityEntity> extends AbstractJPAEntityRepository<String, E> implements SMDIdentityRepository<E> {
    public AbstractJPASMDIdentityRepository(EntityManager em) {
        super(em);
    }

	public void create(E entity) {
        if(entity.getReference()==null || entity.getReference().getId() == null) {
            entity.setReference(SMDIdentityReferenceEntity.forEntity(entity));
            entityManager.persist(entity.getReference());
        }
        super.create(entity);
    }

    public E merge(E entity) {
        if(entity.getReference()==null || entity.getReference().getId() == null) {
            entity.setReference(SMDIdentityReferenceEntity.forEntity(entity));
            entityManager.merge(entity.getReference());
        }
        return super.merge(entity);
    }

	public void remove(E entity) {
        SMDIdentityReference reference = entity.getReference();
        super.remove(entity);
        entityManager.remove(reference);
    }
}