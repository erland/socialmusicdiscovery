package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.ArtistEntity;
import org.socialmusicdiscovery.server.business.model.core.PersonEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPAPersonRepository extends AbstractJPASMDIdentityRepository<PersonEntity> implements PersonRepository {
    ArtistRepository artistRepository;
    @Inject
    public JPAPersonRepository(EntityManager em, ArtistRepository artistRepository) {
        super(em);
        this.artistRepository = artistRepository;
    }

    public Collection<PersonEntity> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<PersonEntity> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" where lower(e.name)=:name");
        query.setParameter("name",name.toLowerCase());
        return query.getResultList();
    }

    public Collection<PersonEntity> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" where lower(e.name) like :name");
        query.setParameter("name","%"+name.toLowerCase()+"%");
        return query.getResultList();
    }
    public void remove(PersonEntity entity) {
        Collection<ArtistEntity> artists = artistRepository.findByPersonWithRelations(entity.getId(), null, null);
        for (Artist artist : artists) {
            artist.setPerson(null);
        }
        entity.getSearchRelations().clear();
        entityManager.createQuery("DELETE from ArtistSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from RecordingSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from ReleaseSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from WorkSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from TrackSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        super.remove(entity);
    }
}
