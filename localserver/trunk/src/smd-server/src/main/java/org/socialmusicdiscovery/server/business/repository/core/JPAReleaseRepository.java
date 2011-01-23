package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPAReleaseRepository extends AbstractJPASMDIdentityRepository<ReleaseEntity> implements ReleaseRepository {
    private ContributorRepository contributorRepository;
    private TrackRepository trackRepository;
    private MediumRepository mediumRepository;
    private LabelRepository labelRepository;
    private ArtistRepository artistRepository;

    @Inject
    public JPAReleaseRepository(EntityManager em, ContributorRepository contributorRepository, TrackRepository trackRepository, MediumRepository mediumRepository, LabelRepository labelRepository, ArtistRepository artistRepository) {
        super(em);
        this.contributorRepository = contributorRepository;
        this.trackRepository = trackRepository;
        this.mediumRepository = mediumRepository;
        this.labelRepository = labelRepository;
        this.artistRepository = artistRepository;
    }

    public Collection<ReleaseEntity> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<ReleaseEntity> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.name)=:name order by e.name");
        query.setParameter("name", name.toLowerCase());
        return query.getResultList();
    }

    public Collection<ReleaseEntity> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.name) like :name order by e.name");
        query.setParameter("name", "%" + name.toLowerCase() + "%");
        return query.getResultList();
    }

    public Collection<ReleaseEntity> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(recordingQueryStringFor("e", "release", "artist", "relation", "searchRelation", mandatoryRelations, optionalRelations, true) + " WHERE searchRelation.reference=:artist order by e.name");
        query.setParameter("artist", artistId);
        return query.getResultList();
    }

    public Collection<ReleaseEntity> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(recordingQueryStringFor("e", "release", "work", "relation", "searchRelation", mandatoryRelations, optionalRelations, true) + " WHERE searchRelation.reference=:work order by e.name");
        query.setParameter("work", workId);
        return query.getResultList();
    }

    @Override
    public void create(ReleaseEntity entity) {
        if (entity.getLabel() != null) {
            if(!entityManager.contains(entity.getLabel())) {
                entity.setLabel(labelRepository.findById(entity.getLabel().getId()));
            }
        }
        for (Medium medium : entity.getMediums()) {
            if(!entityManager.contains(medium)) {
                if(((MediumEntity)medium).getReference()==null || entity.getReference().getId() == null) {
                    ((MediumEntity)medium).setReference(SMDIdentityReferenceEntity.forEntity(medium));
                }
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
    public ReleaseEntity merge(ReleaseEntity entity) {
        if (entity.getLabel() != null) {
            if(!entityManager.contains(entity.getLabel())) {
                entity.setLabel(labelRepository.findById(entity.getLabel().getId()));
            }
        }
        for (Medium medium : entity.getMediums()) {
            if(!entityManager.contains(medium)) {
                mediumRepository.merge((MediumEntity) medium);
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
    public void remove(ReleaseEntity entity) {
        entityManager.refresh(entity);
        for (Track track : entity.getTracks()) {
            track.setRelease(null);
            trackRepository.remove((TrackEntity)track);
        }
        entity.getSearchRelations().clear();
        entityManager.createQuery("DELETE from RecordingReleaseSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();

        entityManager.createNativeQuery("DELETE from classification_references where reference_id=:id").setParameter("id",entity.getId()).executeUpdate();
        super.remove(entity);
    }

}
