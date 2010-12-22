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
    @Inject
    public JPATrackRepository(EntityManager em) {
        super(em);
    }

    public Collection<TrackEntity> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<TrackEntity> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " JOIN e.recording as r LEFT JOIN r.work as w WHERE lower(r.name)=:name or lower(w.name)=:name");
        query.setParameter("name", name.toLowerCase());
        return query.getResultList();
    }

    public Collection<TrackEntity> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " JOIN e.recording as r LEFT JOIN r.work as w WHERE lower(r.name) like :name or lower(w.name) like :name");
        query.setParameter("name", "%" + name.toLowerCase() + "%");
        return query.getResultList();
    }

    public Collection<TrackEntity> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " WHERE e.release=:release");
        ReleaseEntity release = new ReleaseEntity();
        release.setId(releaseId);
        query.setParameter("release", release);
        return query.getResultList();
    }

    public Collection<TrackEntity> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.recording as r JOIN r.searchRelations as sr WHERE sr.reference=:artist order by e.number,e.name");
        query.setParameter("artist", artistId);
        return query.getResultList();
    }

    public Collection<TrackEntity> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.recording as r WHERE r.work=:work");
        WorkEntity work = new WorkEntity();
        work.setId(workId);
        query.setParameter("release", work);
        return query.getResultList();
    }

    @Override
    public void remove(TrackEntity entity) {
        if(entity.getMedium() != null) {
            ((MediumEntity)entity.getMedium()).getTracks().remove(entity);
        }
        if(entity.getRelease() != null) {
            entity.getRelease().getTracks().remove(entity);
        }
        entity.getSearchRelations().clear();
        entityManager.createQuery("DELETE from ArtistSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from PersonSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from RecordingSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from ReleaseSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from WorkSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        super.remove(entity);
    }
}
