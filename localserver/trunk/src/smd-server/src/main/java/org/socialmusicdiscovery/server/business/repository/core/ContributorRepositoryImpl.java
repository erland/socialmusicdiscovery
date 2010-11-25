package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class ContributorRepositoryImpl extends SMDEntityRepositoryImpl<Contributor> implements ContributorRepository {
    @Inject
    public ContributorRepositoryImpl(EntityManager em) {super(em);}

    public Collection<Contributor> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.artist as a WHERE a.id=:artist");
        query.setParameter("artist", artistId);
        return query.getResultList();

    }
}
