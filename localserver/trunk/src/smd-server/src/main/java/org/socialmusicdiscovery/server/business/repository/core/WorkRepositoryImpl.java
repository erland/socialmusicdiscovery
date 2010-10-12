package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;
import org.socialmusicdiscovery.server.business.model.core.Work;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class WorkRepositoryImpl extends SMDEntityRepositoryImpl<Work> implements WorkRepository {
    public WorkRepositoryImpl() {}
    @Inject
    public WorkRepositoryImpl(EntityManager em) {super(em);}

    public Collection<Work> findByName(String name) {
        Query query = entityManager.createQuery("from Work where name=:name");
        query.setParameter("name",name);
        return query.getResultList();
    }
}
