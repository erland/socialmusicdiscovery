package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class ReleaseRepositoryImpl extends SMDEntityRepositoryImpl<Release> implements ReleaseRepository {
    public ReleaseRepositoryImpl() {}
    @Inject
    public ReleaseRepositoryImpl(EntityManager em) {super(em);}

    public Collection<Release> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<Release> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" where e.name=:name");
        query.setParameter("name",name);
        return query.getResultList();
    }
}
