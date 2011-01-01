package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Person;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "persons_search_relations")
public class PersonSearchRelationEntity extends SearchRelationEntity {
    public PersonSearchRelationEntity() {
    }

    public PersonSearchRelationEntity(Person person, Contributor contributor) {
        super(person, contributor);
    }
}
