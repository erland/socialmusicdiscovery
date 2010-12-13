package org.socialmusicdiscovery.server.business.repository;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.GlobalIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class JPAGlobalIdentityRepository extends AbstractJPAEntityRepository<GlobalIdentityEntity, GlobalIdentityEntity> implements GlobalIdentityRepository {
    @Inject
    public JPAGlobalIdentityRepository(EntityManager em) {
        super(em);
    }

    public GlobalIdentityEntity findBySourceAndEntity(String source, SMDIdentity entity) {
        Query query = entityManager.createQuery(queryStringFor("e", null, null, true) + " WHERE e.source=:source and e.entityId=:entityId");
        query.setParameter("source", source);
        query.setParameter("entityId", entity.getId());
        try {
            return (GlobalIdentityEntity) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }
}
