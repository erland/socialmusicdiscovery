package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Work;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class WorkRepositoryImpl extends SMDEntityRepositoryImpl<Work> implements WorkRepository {
    ContributorRepository contributorRepository;

    @Inject
    public WorkRepositoryImpl(EntityManager em, ContributorRepository contributorRepository) {
        super(em);
        this.contributorRepository = contributorRepository;
    }

    public Collection<Work> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<Work> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.name)=:name order by e.name");
        query.setParameter("name", name.toLowerCase());
        return query.getResultList();
    }

    public Collection<Work> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.name) like :name order by e.name");
        query.setParameter("name", "%" + name.toLowerCase() + "%");
        return query.getResultList();
    }

    public Collection<Work> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.searchRelations as r WHERE r.reference=:release order by e.name");
        query.setParameter("release", releaseId);
        return query.getResultList();
    }

    public Collection<Work> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.searchRelations as r WHERE r.reference=:artist order by e.name");
        query.setParameter("artist", artistId);
        return query.getResultList();
    }
    public void remove(Work entity) {
        for (Contributor contributor : entity.getContributors()) {
            contributorRepository.remove(contributor);
        }
        entity.getSearchRelations().clear();
        entityManager.createQuery("DELETE from ArtistSearchRelation where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from PersonSearchRelation where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from RecordingSearchRelation where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from ReleaseSearchRelation where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        super.remove(entity);
    }
}
