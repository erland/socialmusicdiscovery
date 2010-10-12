package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;
import org.socialmusicdiscovery.server.business.model.core.Label;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class LabelRepositoryImpl extends SMDEntityRepositoryImpl<Label> implements LabelRepository {
    public LabelRepositoryImpl() {}
    @Inject
    public LabelRepositoryImpl(EntityManager em) {super(em);}

    public Collection<Label> findByName(String name) {
        Query query = entityManager.createQuery("from Label where name=:name");
        query.setParameter("name",name);
        return query.getResultList();
    }
}
