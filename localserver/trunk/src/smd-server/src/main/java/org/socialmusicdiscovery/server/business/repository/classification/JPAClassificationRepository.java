package org.socialmusicdiscovery.server.business.repository.classification;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPAClassificationRepository extends AbstractJPASMDIdentityRepository<ClassificationEntity> implements ClassificationRepository {
    @Inject
    public JPAClassificationRepository(EntityManager em) {super(em);}

    public Collection<ClassificationEntity> findByNameAndType(String name, String type) {
        Query query = entityManager.createQuery("from ClassificationEntity where name=:name and type=:type");
        query.setParameter("name",name);
        query.setParameter("type",type);
        return query.getResultList();
    }

    public Collection<ClassificationEntity> findByReference(String reference) {
        Query query = entityManager.createQuery("select e from ClassificationEntity as e JOIN e.references as r where r.id=:reference");
        query.setParameter("reference",reference);
        return query.getResultList();
    }

    public Collection<ClassificationEntity> findByTypeAndReference(String type, String reference) {
        Query query = entityManager.createQuery("from ClassificationEntity JOIN references as r where r.type=:type and r.id=:reference");
        query.setParameter("type",type);
        query.setParameter("reference",reference);
        return query.getResultList();
    }
}
