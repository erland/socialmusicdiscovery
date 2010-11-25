package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Medium;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class ReleaseRepositoryImpl extends SMDEntityRepositoryImpl<Release> implements ReleaseRepository {
    private ContributorRepository contributorRepository;
    private TrackRepository trackRepository;
    private MediumRepository mediumRepository;

    @Inject
    public ReleaseRepositoryImpl(EntityManager em, ContributorRepository contributorRepository, TrackRepository trackRepository, MediumRepository mediumRepository) {
        super(em);
        this.contributorRepository = contributorRepository;
        this.trackRepository = trackRepository;
        this.mediumRepository = mediumRepository;
    }

    public Collection<Release> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<Release> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.name)=:name order by e.name");
        query.setParameter("name", name.toLowerCase());
        return query.getResultList();
    }

    public Collection<Release> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.name) like :name order by e.name");
        query.setParameter("name", "%" + name.toLowerCase() + "%");
        return query.getResultList();
    }

    public Collection<Release> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.searchRelations as r WHERE r.reference=:artist order by e.name");
        query.setParameter("artist", artistId);
        return query.getResultList();
    }

    public Collection<Release> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.searchRelations as r WHERE r.reference=:work order by e.name");
        query.setParameter("work", workId);
        return query.getResultList();
    }
    public void remove(Release entity) {
        for (Contributor contributor : entity.getContributors()) {
            contributorRepository.remove(contributor);
        }
        for (Track track : entity.getTracks()) {
            track.setRelease(null);
            trackRepository.remove(track);
        }
        for (Medium medium : entity.getMediums()) {
            mediumRepository.remove(medium);
        }
        entity.getSearchRelations().clear();
        entityManager.createQuery("DELETE from ArtistSearchRelation where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from PersonSearchRelation where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from RecordingSearchRelation where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from WorkSearchRelation where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        super.remove(entity);
    }

}
