package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;
import org.socialmusicdiscovery.server.business.model.core.Recording;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class RecordingRepositoryImpl extends SMDEntityRepositoryImpl<Recording> implements RecordingRepository {
    public RecordingRepositoryImpl() {}
    @Inject
    public RecordingRepositoryImpl(EntityManager em) {super(em);}

    public Collection<Recording> findByName(String name) {
        Query query = entityManager.createQuery("from Recording where name=:name");
        query.setParameter("name",name);
        return query.getResultList();
    }
}
