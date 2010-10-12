package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;
import org.socialmusicdiscovery.server.business.model.core.Person;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class PersonRepositoryImpl extends SMDEntityRepositoryImpl<Person> implements PersonRepository {
    public PersonRepositoryImpl() {}
    @Inject
    public PersonRepositoryImpl(EntityManager em) {super(em);}

    public Collection<Person> findByName(String name) {
        Query query = entityManager.createQuery("from Person where name=:name");
        query.setParameter("name",name);
        return query.getResultList();
    }
}
