package org.socialmusicdiscovery.server.business.repository;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.GlobalIdentity;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class GlobalIdentityRepositoryImpl extends EntityRepositoryImpl<GlobalIdentity, GlobalIdentity> implements GlobalIdentityRepository {
    @Inject
    public GlobalIdentityRepositoryImpl(EntityManager em) {
        super(em);
    }

    public GlobalIdentity findBySourceAndEntity(String source, SMDEntity entity) {
        Query query = entityManager.createQuery(queryStringFor("e", null, null, true) + " WHERE e.source=:source and e.entityId=:entityId");
        query.setParameter("source", source);
        query.setParameter("entityId", entity.getId());
        try {
            return (GlobalIdentity) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }
}
