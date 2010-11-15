package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.server.business.model.core.Work;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class TrackRepositoryImpl extends SMDEntityRepositoryImpl<Track> implements TrackRepository {
    public TrackRepositoryImpl() {
    }

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
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " WHERE t.release_id=:releaseId");
        query.setParameter("releaseId", releaseId);
        return query.getResultList();
    }

    public Collection<Track> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.recording as r LEFT JOIN r.contributors as c1 LEFT JOIN r.work as w LEFT JOIN w.contributors as c2 WHERE " +
                " c1.artist=:recordingArtist " +
                " or c2.artist=:workArtist");
        Artist artist = new Artist();
        artist.setId(artistId);
        query.setParameter("recordingArtist", artist);
        query.setParameter("workArtist", artist);
        return query.getResultList();
    }

    public Collection<Track> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.recording as r WHERE r.work=:work");
        Work work = new Work();
        work.setId(workId);
        query.setParameter("release", work);
        return query.getResultList();
    }
}
