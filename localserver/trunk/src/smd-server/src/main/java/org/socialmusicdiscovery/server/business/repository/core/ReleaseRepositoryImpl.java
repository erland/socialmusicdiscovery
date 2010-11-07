package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Work;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class ReleaseRepositoryImpl extends SMDEntityRepositoryImpl<Release> implements ReleaseRepository {
    public ReleaseRepositoryImpl() {}
    @Inject
    public ReleaseRepositoryImpl(EntityManager em) {super(em);}

    public Collection<Release> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<Release> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" where lower(e.name)=:name");
        query.setParameter("name",name.toLowerCase());
        return query.getResultList();
    }

    public Collection<Release> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" where lower(e.name) like :name");
        query.setParameter("name","%"+name.toLowerCase()+"%");
        return query.getResultList();
    }

    public Collection<Release> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations, true)+" LEFT JOIN e.contributors c WHERE c.artist=:releaseArtist"+
               " OR EXISTS (select rel from Release as rel JOIN rel.tracks as t JOIN t.recording as r JOIN r.contributors as c WHERE c.artist=:recordingArtist and rel.id=e.id)" +
               " OR EXISTS (select rel from Release as rel JOIN rel.tracks as t JOIN t.recording as r JOIN r.work as w JOIN w.contributors WHERE c.artist=:workArtist and rel.id=e.id)");
        Artist artist = new Artist();
        artist.setId(artistId);
        query.setParameter("releaseArtist",artist);
        query.setParameter("recordingArtist",artist);
        query.setParameter("workArtist",artist);
        return query.getResultList();
    }

    public Collection<Release> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations, true)+
                " JOIN e.tracks as t JOIN t.recording as r where r.work=:work");
        Work work = new Work();
        work.setId(workId);
        query.setParameter("work",work);
        return query.getResultList();
    }

}
