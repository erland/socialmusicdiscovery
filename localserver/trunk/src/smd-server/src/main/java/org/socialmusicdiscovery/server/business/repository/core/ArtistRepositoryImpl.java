package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Person;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class ArtistRepositoryImpl extends SMDEntityRepositoryImpl<Artist> implements ArtistRepository {
    private PersonRepository personRepository;
    private ContributorRepository contributorRepository;

    @Inject
    public ArtistRepositoryImpl(EntityManager em, PersonRepository personRepository, ContributorRepository contributorRepository) {
        super(em);
        this.personRepository = personRepository;
        this.contributorRepository = contributorRepository;
    }

    public Collection<Artist> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<Artist> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.name)=:name order by e.name");
        query.setParameter("name", name.toLowerCase());
        return query.getResultList();
    }

    public Collection<Artist> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.name) like :name order by e.name");
        query.setParameter("name", "%" + name.toLowerCase() + "%");
        return query.getResultList();
    }

    @Override
    public Artist merge(Artist entity) {
        if (entity.getPerson() != null && entity.getPerson().getId() != null) {
            Person person = personRepository.findById(entity.getPerson().getId());
            entity.setPerson(person);
        }
        return super.merge(entity);
    }

    public Collection<Artist> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.searchRelations as r WHERE r.reference=:work order by e.name");
        query.setParameter("work", workId);
        return query.getResultList();
    }

    public Collection<Artist> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.searchRelations as r WHERE r.reference=:release order by e.name");
        query.setParameter("release", releaseId);
        return query.getResultList();
    }

    public Collection<Artist> findByPersonWithRelations(String personId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.person as p WHERE p.id=:person");
        query.setParameter("person", personId);
        return query.getResultList();
    }

    public void remove(Artist entity) {
        Collection<Contributor> contributors = contributorRepository.findByArtistWithRelations(entity.getId(),null,null);
        for (Contributor contributor : contributors) {
            contributorRepository.remove(contributor);
        }

        entity.getSearchRelations().clear();
        entityManager.createQuery("DELETE from RecordingSearchRelation where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from ReleaseSearchRelation where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from PersonSearchRelation where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from WorkSearchRelation where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        super.remove(entity);
    }
}
