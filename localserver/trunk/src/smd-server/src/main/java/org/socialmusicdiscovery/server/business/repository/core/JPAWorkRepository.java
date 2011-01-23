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
        Query query = entityManager.createQuery(recordingQueryStringFor("e", "work", "release", "relation", "searchRelation", mandatoryRelations, optionalRelations, true) + " WHERE searchRelation.reference=:release order by e.name");
        query.setParameter("release", releaseId);
        return query.getResultList();
    }

    public Collection<WorkEntity> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(recordingQueryStringFor("e", "work", "artist", "relation", "searchRelation", mandatoryRelations, optionalRelations, true) + " WHERE searchRelation.reference=:artist order by e.name");
        query.setParameter("artist", artistId);
        return query.getResultList();
    }
    @Override
    public void create(WorkEntity entity) {
        if (entity.getParent() != null) {
            if(!entityManager.contains(entity.getParent())) {
                entity.setParent(findById(entity.getParent().getId()));
            }
        }
        for (Contributor contributor : entity.getContributors()) {
            if(!entityManager.contains(contributor)) {
                contributorRepository.create((ContributorEntity) contributor);
            }
        }
        super.create(entity);
    }

    @Override
    public WorkEntity merge(WorkEntity entity) {
        if (entity.getParent() != null) {
            if(!entityManager.contains(entity.getParent())) {
                entity.setParent(findById(entity.getParent().getId()));
            }
        }
        for (Contributor contributor : entity.getContributors()) {
            if(!entityManager.contains(contributor)) {
                contributorRepository.merge((ContributorEntity) contributor);
            }
        }
        return super.merge(entity);
    }


    @Override
    public void remove(WorkEntity entity) {
        entityManager.createQuery("DELETE from RecordingWorkSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from ReleaseSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();

        entityManager.createNativeQuery("DELETE from classification_references where reference_id=:id").setParameter("id",entity.getId()).executeUpdate();
        super.remove(entity);
    }
}
