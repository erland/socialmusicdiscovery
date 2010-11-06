package org.socialmusicdiscovery.server.business.repository.classification;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;
import org.socialmusicdiscovery.server.business.model.classification.Classification;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class ClassificationRepositoryImpl extends SMDEntityRepositoryImpl<Classification> implements ClassificationRepository {
    public ClassificationRepositoryImpl() {}
    @Inject
    public ClassificationRepositoryImpl(EntityManager em) {super(em);}

    public Collection<Classification> findByNameAndType(String name, String type) {
        Query query = entityManager.createQuery("from Classification where name=:name and type=:type");
        query.setParameter("name",name);
        query.setParameter("type",type);
        return query.getResultList();
    }
}
