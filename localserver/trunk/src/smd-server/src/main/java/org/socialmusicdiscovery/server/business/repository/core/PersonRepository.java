package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.core.Person;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;

import java.util.Collection;

@ImplementedBy(PersonRepositoryImpl.class)
public interface PersonRepository extends SMDEntityRepository<Person> {
    Collection<Person> findByName(String name);
    Collection<Person> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
}
