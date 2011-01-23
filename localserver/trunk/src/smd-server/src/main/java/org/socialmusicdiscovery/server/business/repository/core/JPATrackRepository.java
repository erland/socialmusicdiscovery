package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.MediumEntity;
import org.socialmusicdiscovery.server.business.model.core.ReleaseEntity;
import org.socialmusicdiscovery.server.business.model.core.TrackEntity;
import org.socialmusicdiscovery.server.business.model.core.WorkEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPATrackRepository extends AbstractJPASMDIdentityRepository<TrackEntity> implements TrackRepository {
    private ReleaseRepository releaseRepository;
    private MediumRepository mediumRepository;
    private RecordingRepository recordingRepository;

    @Inject
    public JPATrackRepository(EntityManager em, ReleaseRepository releaseRepository, MediumRepository mediumRepository, RecordingRepository recordingRepository) {
        super(em);
        this.releaseRepository = releaseRepository;
        this.mediumRepository = mediumRepository;
        this.recordingRepository = recordingRepository;
    }

    public Collection<TrackEntity> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<TrackEntity> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " JOIN e.recording as r LEFT JOIN r.works as w WHERE lower(r.name)=:name or lower(w.name)=:name");
        query.setParameter("name", name.toLowerCase());
        return query.getResultList();
    }

    public Collection<TrackEntity> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " JOIN e.recording as r LEFT JOIN r.works as w WHERE lower(r.name) like :name or lower(w.name) like :name");
        query.setParameter("name", "%" + name.toLowerCase() + "%");
        return query.getResultList();
    }

    public Collection<TrackEntity> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        String order = "e.number";
        if(optionalRelations != null && optionalRelations.contains("medium")) {
            order = "medium.name,medium.number,e.number";
        }
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " WHERE e.release=:release order by "+order);
        ReleaseEntity release = new ReleaseEntity();
        release.setId(releaseId);
        query.setParameter("release", release);
        return query.getResultList();
    }

    public Collection<TrackEntity> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.recording as recording JOIN recording.artistSearchRelations as sr WHERE sr.reference=:artist order by e.number,e.name");
        query.setParameter("artist", artistId);
        return query.getResultList();
    }

    public Collection<TrackEntity> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.recording as recording JOIN recording.works as works WHERE works.id=:work");
        query.setParameter("work", workId);
        return query.getResultList();
    }

    @Override
    public void create(TrackEntity entity) {
        if(entity.getRelease() != null) {
            if(!entityManager.contains(entity.getRelease())) {
                entity.setRelease(releaseRepository.findById(entity.getRelease().getId()));
            }
        }
        if (entity.getMedium() != null) {
            if(!entityManager.contains(entity.getMedium())) {
                entity.setMedium(mediumRepository.findById(entity.getMedium().getId()));
            }
        }
        if (entity.getRecording() != null) {
            if(!entityManager.contains(entity.getRecording())) {
                entity.setRecording(recordingRepository.findById(entity.getRecording().getId()));
            }
        }
        super.create(entity);
    }

    @Override
    public TrackEntity merge(TrackEntity entity) {
        if (entity.getRelease() != null) {
            if(!entityManager.contains(entity.getRelease())) {
                entity.setRelease(releaseRepository.findById(entity.getRelease().getId()));
            }
        }
        if (entity.getMedium() != null) {
            if(!entityManager.contains(entity.getMedium())) {
                entity.setMedium(mediumRepository.findById(entity.getMedium().getId()));
            }
        }
        if (entity.getRecording() != null) {
            if(!entityManager.contains(entity.getRecording())) {
                entity.setRecording(recordingRepository.findById(entity.getRecording().getId()));
            }
        }
        return super.merge(entity);
    }

    @Override
    public void remove(TrackEntity entity) {
        if(entity.getMedium() != null) {
            ((MediumEntity)entity.getMedium()).getTracks().remove(entity);
        }
        if(entity.getRelease() != null) {
            entity.getRelease().getTracks().remove(entity);
        }
        entityManager.createQuery("DELETE from RecordingTrackSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from ReleaseSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        super.remove(entity);
    }
}
