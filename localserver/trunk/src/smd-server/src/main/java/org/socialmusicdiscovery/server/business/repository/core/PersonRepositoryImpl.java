package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Person;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class PersonRepositoryImpl extends SMDEntityRepositoryImpl<Person> implements PersonRepository {
    ArtistRepository artistRepository;
    @Inject
    public PersonRepositoryImpl(EntityManager em, ArtistRepository artistRepository) {
        super(em);
        this.artistRepository = artistRepository;
    }

    public Collection<Person> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<Person> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" where lower(e.name)=:name");
        query.setParameter("name",name.toLowerCase());
        return query.getResultList();
    }

    public Collection<Person> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" where lower(e.name) like :name");
        query.setParameter("name","%"+name.toLowerCase()+"%");
        return query.getResultList();
    }
    public void remove(Person entity) {
        Collection<Artist> artists = artistRepository.findByPersonWithRelations(entity.getId(), null, null);
        for (Artist artist : artists) {
            artist.setPerson(null);
        }
        entity.getSearchRelations().clear();
        entityManager.createQuery("DELETE from ArtistSearchRelation where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from RecordingSearchRelation where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from ReleaseSearchRelation where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from WorkSearchRelation where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        super.remove(entity);
    }
}
