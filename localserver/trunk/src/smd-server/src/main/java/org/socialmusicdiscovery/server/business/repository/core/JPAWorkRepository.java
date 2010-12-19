package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.ContributorEntity;
import org.socialmusicdiscovery.server.business.model.core.WorkEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPAWorkRepository extends AbstractJPASMDIdentityRepository<WorkEntity> implements WorkRepository {
    ContributorRepository contributorRepository;

    @Inject
    public JPAWorkRepository(EntityManager em, ContributorRepository contributorRepository) {
        super(em);
        this.contributorRepository = contributorRepository;
    }

    public Collection<WorkEntity> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<WorkEntity> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.name)=:name order by e.name");
        query.setParameter("name", name.toLowerCase());
        return query.getResultList();
    }

    public Collection<WorkEntity> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.name) like :name order by e.name");
        query.setParameter("name", "%" + name.toLowerCase() + "%");
        return query.getResultList();
    }

    public Collection<WorkEntity> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.searchRelations as r WHERE r.reference=:release order by e.name");
        query.setParameter("release", releaseId);
        return query.getResultList();
    }

    public Collection<WorkEntity> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.searchRelations as r WHERE r.reference=:artist order by e.name");
        query.setParameter("artist", artistId);
        return query.getResultList();
    }
    public void remove(WorkEntity entity) {
        for (Contributor contributor : entity.getContributors()) {
            contributorRepository.remove((ContributorEntity)contributor);
        }
        entity.getSearchRelations().clear();
        entityManager.createQuery("DELETE from ArtistSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from PersonSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from RecordingSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from ReleaseSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        super.remove(entity);
    }
}