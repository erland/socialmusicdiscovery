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
        }
        super.create(entity);
    }

    public E merge(E entity) {
        if(entity.getReference()==null || entity.getReference().getId() == null) {
            entity.setReference(SMDIdentityReferenceEntity.forEntity(entity));
        }
        return super.merge(entity);
    }

	public void remove(E entity) {
        super.remove(entity);
    }
}