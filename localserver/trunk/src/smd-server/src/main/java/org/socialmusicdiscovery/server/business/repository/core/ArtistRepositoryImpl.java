package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Person;
import org.socialmusicdiscovery.server.business.model.core.Work;
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
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" where lower(e.name)=:name");
        query.setParameter("name",name.toLowerCase());
        return query.getResultList();
    }

    public Collection<Artist> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" where lower(e.name) like :name");
        query.setParameter("name","%"+name.toLowerCase()+"%");
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

    public Collection<Artist> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations, true)+
                " WHERE EXISTS (select w from Work as w JOIN w.contributors as c where w.id=:workId and c.artist=e.id) "+
                " OR EXISTS (select r from Recording as r JOIN r.contributors as c where r.work=:work and c.artist=e.id)");
        Work work = new Work();
        work.setId(workId);
        query.setParameter("work",work);
        query.setParameter("workId",workId);
        return query.getResultList();
    }
    public Collection<Artist> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations, true)+
                " WHERE" +
                " EXISTS (select rel from Release as rel JOIN rel.contributors as c1 where c1.artist=e.id and rel.id=:releaseId)" +
                " OR EXISTS (select rel from Release as rel JOIN rel.tracks as t JOIN t.recording as r JOIN r.contributors as c2 WHERE c2.artist=e.id and rel.id=:releaseId)" +
                " OR EXISTS (select rel from Release as rel JOIN rel.tracks as t JOIN t.recording as r JOIN r.work as w JOIN w.contributors as c3 WHERE c3.artist=e.id and rel.id=:releaseId)");
        query.setParameter("releaseId",releaseId);
        return query.getResultList();
    }

}
