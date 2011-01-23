package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.ContributorEntity;
import org.socialmusicdiscovery.server.business.model.core.RecordingEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPARecordingRepository extends AbstractJPASMDIdentityRepository<RecordingEntity> implements RecordingRepository {
    ContributorRepository contributorRepository;
    WorkRepository workRepository;

    @Inject
    public JPARecordingRepository(EntityManager em, ContributorRepository contributorRepository, WorkRepository workRepository) {
        super(em);
        this.contributorRepository = contributorRepository;
        this.workRepository = workRepository;
    }

    public Collection<RecordingEntity> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<RecordingEntity> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" where lower(e.name)=:name");
        query.setParameter("name",name.toLowerCase());
        return query.getResultList();
    }

    public Collection<RecordingEntity> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" where lower(e.name) like :name");
        query.setParameter("name","%"+name.toLowerCase()+"%");
        return query.getResultList();
    }
    @Override
    public void create(RecordingEntity entity) {
        if (entity.getMixOf() != null) {
            if(!entityManager.contains(entity.getMixOf())) {
                entity.setMixOf(findById(entity.getMixOf().getId()));
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
    public RecordingEntity merge(RecordingEntity entity) {
        if (entity.getMixOf() != null) {
            if(!entityManager.contains(entity.getMixOf())) {
                entity.setMixOf(findById(entity.getMixOf().getId()));
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
    public void remove(RecordingEntity entity) {
        entity.getLabelSearchRelations().clear();
        entity.getReleaseSearchRelations().clear();
        entity.getTrackSearchRelations().clear();
        entity.getWorkSearchRelations().clear();
        entity.getArtistSearchRelations().clear();
        entity.getClassificationSearchRelations().clear();
        entityManager.createQuery("DELETE from ReleaseSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();

        entityManager.createNativeQuery("DELETE from classification_references where reference_id=:id").setParameter("id",entity.getId()).executeUpdate();
        super.remove(entity);
    }
}
