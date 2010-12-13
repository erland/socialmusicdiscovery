package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPAReleaseRepository extends AbstractJPASMDIdentityRepository<ReleaseEntity> implements ReleaseRepository {
    private ContributorRepository contributorRepository;
    private TrackRepository trackRepository;
    private MediumRepository mediumRepository;

    @Inject
    public JPAReleaseRepository(EntityManager em, ContributorRepository contributorRepository, TrackRepository trackRepository, MediumRepository mediumRepository) {
        super(em);
        this.contributorRepository = contributorRepository;
        this.trackRepository = trackRepository;
        this.mediumRepository = mediumRepository;
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
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.searchRelations as r WHERE r.reference=:artist order by e.name");
        query.setParameter("artist", artistId);
        return query.getResultList();
    }

    public Collection<ReleaseEntity> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.searchRelations as r WHERE r.reference=:work order by e.name");
        query.setParameter("work", workId);
        return query.getResultList();
    }
    public void remove(ReleaseEntity entity) {
        for (Contributor contributor : entity.getContributors()) {
            contributorRepository.remove((ContributorEntity)contributor);
        }
        for (Track track : entity.getTracks()) {
            track.setRelease(null);
            trackRepository.remove((TrackEntity)track);
        }
        for (Medium medium : entity.getMediums()) {
            mediumRepository.remove((MediumEntity)medium);
        }
        entity.getSearchRelations().clear();
        entityManager.createQuery("DELETE from ArtistSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from PersonSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from RecordingSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from WorkSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        super.remove(entity);
    }

}
