package org.socialmusicdiscovery.server.business.repository.subjective;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReference;
import org.socialmusicdiscovery.server.business.model.subjective.RelationEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPAEntityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPARelationRepository extends AbstractJPAEntityRepository<RelationEntity, RelationEntity> implements RelationRepository {
    @Inject
    public JPARelationRepository(EntityManager em) {super(em);}

    public Collection<RelationEntity> findRelationsFrom(SMDIdentityReference reference) {
        Query query = entityManager.createQuery("from RelationEntity where from_id=:from");
        query.setParameter("from",reference.getId());
        Collection<RelationEntity> result = query.getResultList();
        return result;
    }

    public Collection<RelationEntity> findRelationsFrom(SMDIdentityReference reference, Class relatedTo) {
        Query query = entityManager.createQuery("select r from RelationEntity r,SMDIdentityReferenceEntity ref where r.fromId=:from and r.toId=ref.id and ref.type=:type");
        query.setParameter("from",reference.getId());
        query.setParameter("type",relatedTo.getName());
        Collection<RelationEntity> result = query.getResultList();
        return result;
    }

    public Collection<RelationEntity> findRelationsTo(SMDIdentityReference reference) {
        Query query = entityManager.createQuery("from RelationEntity where to_id=:to");
        query.setParameter("to",reference.getId());
        Collection<RelationEntity> result = query.getResultList();
        return result;
    }

    public Collection<RelationEntity> findRelationsTo(SMDIdentityReference reference, Class relatedFrom) {
        Query query = entityManager.createQuery("r from RelationEntity r,SMDIdentityReferenceEntity ref where r.toId=:to and r.fromId=ref.id and ref.type=:relationType");
        query.setParameter("to",reference.getId());
        query.setParameter("relationType",relatedFrom.getName());
        Collection<RelationEntity> result = query.getResultList();
        return result;
    }
}
