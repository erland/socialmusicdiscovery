package org.socialmusicdiscovery.server.business.repository.subjective;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.SMDEntityReference;
import org.socialmusicdiscovery.server.business.model.subjective.Relation;
import org.socialmusicdiscovery.server.business.repository.EntityRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class RelationRepositoryImpl extends EntityRepositoryImpl<Relation, Relation> implements RelationRepository {
    @Inject
    public RelationRepositoryImpl(EntityManager em) {super(em);}

    public Collection<Relation> findRelationsFrom(SMDEntityReference reference) {
        Query query = entityManager.createQuery("from Relation where from_id=:from");
        query.setParameter("from",reference.getId());
        Collection<Relation> result = query.getResultList();
        return result;
    }

    public Collection<Relation> findRelationsFrom(SMDEntityReference reference, Class relatedTo) {
        Query query = entityManager.createQuery("select r from Relation r,SMDEntityReference ref where r.fromId=:from and r.toId=ref.id and ref.type=:type");
        query.setParameter("from",reference.getId());
        query.setParameter("type",relatedTo.getName());
        Collection<Relation> result = query.getResultList();
        return result;
    }

    public Collection<Relation> findRelationsTo(SMDEntityReference reference) {
        Query query = entityManager.createQuery("from Relation where to_id=:to");
        query.setParameter("to",reference.getId());
        Collection<Relation> result = query.getResultList();
        return result;
    }

    public Collection<Relation> findRelationsTo(SMDEntityReference reference, Class relatedFrom) {
        Query query = entityManager.createQuery("r from Relation r,SMDEntityReference ref where r.toId=:to and r.fromId=ref.id and ref.type=:relationType");
        query.setParameter("to",reference.getId());
        query.setParameter("relationType",relatedFrom.getName());
        Collection<Relation> result = query.getResultList();
        return result;
    }
}
