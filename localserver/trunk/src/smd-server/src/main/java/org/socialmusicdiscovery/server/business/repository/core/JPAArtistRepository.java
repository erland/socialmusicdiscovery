package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.ArtistEntity;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.ContributorEntity;
import org.socialmusicdiscovery.server.business.model.core.PersonEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPAArtistRepository extends AbstractJPASMDIdentityRepository<ArtistEntity> implements ArtistRepository {
    private PersonRepository personRepository;
    private ContributorRepository contributorRepository;

    @Inject
    public JPAArtistRepository(EntityManager em, PersonRepository personRepository, ContributorRepository contributorRepository) {
        super(em);
        this.personRepository = personRepository;
        this.contributorRepository = contributorRepository;
    }

    public Collection<ArtistEntity> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<ArtistEntity> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.name)=:name order by e.name");
        query.setParameter("name", name.toLowerCase());
        return query.getResultList();
    }

    public Collection<ArtistEntity> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.name) like :name order by e.name");
        query.setParameter("name", "%" + name.toLowerCase() + "%");
        return query.getResultList();
    }

    @Override
    public ArtistEntity merge(ArtistEntity entity) {
        if (entity.getPerson() != null && entity.getPerson().getId() != null) {
            PersonEntity person = personRepository.findById(entity.getPerson().getId());
            entity.setPerson(person);
        }
        return super.merge(entity);
    }

    public Collection<ArtistEntity> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(recordingQueryStringFor("e", "artist", "work", "relation", "searchRelation", mandatoryRelations, optionalRelations, true) + " WHERE searchRelation.reference=:work order by e.name");
        query.setParameter("work", workId);
        return query.getResultList();
    }

    public Collection<ArtistEntity> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(recordingQueryStringFor("e", "artist", "release", "relation", "searchRelation", mandatoryRelations, optionalRelations, true) + " WHERE searchRelation.reference=:release order by e.name");
        query.setParameter("release", releaseId);
        return query.getResultList();
    }

    public Collection<ArtistEntity> findByPersonWithRelations(String personId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.person as p WHERE p.id=:person");
        query.setParameter("person", personId);
        return query.getResultList();
    }

    public void remove(ArtistEntity entity) {
        Collection<ContributorEntity> contributors = contributorRepository.findByArtistWithRelations(entity.getId(),null,null);
        for (Contributor contributor : contributors) {
            contributorRepository.remove((ContributorEntity)contributor);
        }

        entityManager.createQuery("DELETE from RecordingArtistSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from ReleaseSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from PersonSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        super.remove(entity);
    }
}
