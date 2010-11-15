package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Work;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class WorkRepositoryImpl extends SMDEntityRepositoryImpl<Work> implements WorkRepository {
    public WorkRepositoryImpl() {
    }

    @Inject
    public WorkRepositoryImpl(EntityManager em) {
        super(em);
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
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " WHERE EXISTS (select r from Release as r JOIN r.tracks as t JOIN t.recording as rec WHERE r=:release and rec.work=e.id) order by e.name");
        Release release = new Release();
        release.setId(releaseId);
        query.setParameter("release", release);
        return query.getResultList();
    }

    public Collection<Work> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " LEFT JOIN e.contributors as c1 WHERE " +
                "EXISTS (select r from Recording as r JOIN r.contributors as c WHERE c.artist=:recordingArtist and r.work=e.id) " +
                "or EXISTS (select r from Release as r JOIN r.contributors as c JOIN r.tracks as t JOIN t.recording as r JOIN r.work WHERE c.artist=:releaseArtist and r.work=e.id) " +
                "or c1.artist=:workArtist order by e.name");
        Artist artist = new Artist();
        artist.setId(artistId);
        query.setParameter("releaseArtist", artist);
        query.setParameter("recordingArtist", artist);
        query.setParameter("workArtist", artist);
        return query.getResultList();
    }
}
