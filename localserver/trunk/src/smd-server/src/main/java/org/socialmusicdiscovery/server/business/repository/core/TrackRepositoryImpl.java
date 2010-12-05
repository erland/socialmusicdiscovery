package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.server.business.model.core.Work;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class TrackRepositoryImpl extends SMDEntityRepositoryImpl<Track> implements TrackRepository {
    @Inject
    public TrackRepositoryImpl(EntityManager em) {
        super(em);
    }

    public Collection<Track> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<Track> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " JOIN e.recording as r LEFT JOIN r.work as w WHERE lower(r.name)=:name or lower(w.name)=:name");
        query.setParameter("name", name.toLowerCase());
        return query.getResultList();
    }

    public Collection<Track> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " JOIN e.recording as r LEFT JOIN r.work as w WHERE lower(r.name) like :name or lower(w.name) like :name");
        query.setParameter("name", "%" + name.toLowerCase() + "%");
        return query.getResultList();
    }

    public Collection<Track> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " WHERE e.release=:release");
        Release release = new Release();
        release.setId(releaseId);
        query.setParameter("release", release);
        return query.getResultList();
    }

    public Collection<Track> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.recording as r JOIN r.searchRelations as sr WHERE sr.reference=:artist order by e.number,e.name");
        query.setParameter("artist", artistId);
        return query.getResultList();
    }

    public Collection<Track> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.recording as r WHERE r.work=:work");
        Work work = new Work();
        work.setId(workId);
        query.setParameter("release", work);
        return query.getResultList();
    }

    @Override
    public void remove(Track entity) {
        if(entity.getMedium() != null) {
            entity.getMedium().getTracks().remove(entity);
        }
        if(entity.getRelease() != null) {
            entity.getRelease().getTracks().remove(entity);
        }
        super.remove(entity);
    }
}
