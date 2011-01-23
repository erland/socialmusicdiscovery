package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.ContributorEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPAContributorRepository extends AbstractJPASMDIdentityRepository<ContributorEntity> implements ContributorRepository {
    private ArtistRepository artistRepository;

    @Inject
    public JPAContributorRepository(EntityManager em, ArtistRepository artistRepository) {
        super(em);
        this.artistRepository = artistRepository;
    }

    public Collection<ContributorEntity> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.artist as a WHERE a.id=:artist");
        query.setParameter("artist", artistId);
        return query.getResultList();

    }

    public Collection<ContributorEntity> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        StringBuffer queryString = new StringBuffer(200);
        String distinct = "distinct ";
        queryString.append("select ").append(distinct).append("e").append(" from ").append("ReleaseEntity as r JOIN r.contributors as e");
        if(mandatoryRelations != null) {
            for (String relation : mandatoryRelations) {
                queryString.append(" JOIN FETCH ").append("e").append(".").append(relation);
            }
        }
        if(optionalRelations != null) {
            for (String relation : optionalRelations) {
                queryString.append(" LEFT JOIN FETCH ").append("e").append(".").append(relation);
            }
        }

        Query query = entityManager.createQuery(queryString.toString()+" WHERE r.id=:release");
        query.setParameter("release", releaseId);
        return query.getResultList();

    }
    @Override
    public void create(ContributorEntity entity) {
        if (entity.getArtist() != null) {
            if(!entityManager.contains(entity.getArtist())) {
                entity.setArtist(artistRepository.findById(entity.getArtist().getId()));
            }
        }
        super.create(entity);
    }

    @Override
    public ContributorEntity merge(ContributorEntity entity) {
        if (entity.getArtist() != null) {
            if(!entityManager.contains(entity.getArtist())) {
                entity.setArtist(artistRepository.findById(entity.getArtist().getId()));
            }
        }
        return super.merge(entity);
    }

}
