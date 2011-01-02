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

    @Inject
    public JPARecordingRepository(EntityManager em, ContributorRepository contributorRepository) {
        super(em);
        this.contributorRepository = contributorRepository;
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
    public void remove(RecordingEntity entity) {
        for (Contributor contributor : entity.getContributors()) {
            contributorRepository.remove((ContributorEntity)contributor);
        }
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
