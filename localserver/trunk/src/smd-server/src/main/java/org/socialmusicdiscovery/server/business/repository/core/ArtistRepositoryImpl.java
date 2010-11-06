package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Person;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class ArtistRepositoryImpl extends SMDEntityRepositoryImpl<Artist> implements ArtistRepository {
    private PersonRepository personRepository;

    public ArtistRepositoryImpl() {
        personRepository = new PersonRepositoryImpl();
    }
    @Inject
    public ArtistRepositoryImpl(EntityManager em) {
        super(em);
        personRepository = new PersonRepositoryImpl(em);
    }

    public Collection<Artist> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<Artist> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" where e.name=:name");
        query.setParameter("name",name);
        return query.getResultList();
    }

    @Override
    public Artist merge(Artist entity) {
        if(entity.getPerson() != null && entity.getPerson().getId() != null) {
            Person person = personRepository.findById(entity.getPerson().getId());
            entity.setPerson(person);
        }
        return super.merge(entity);
    }
}
