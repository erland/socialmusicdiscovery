package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "persons_search_relations")
public class PersonSearchRelation extends SearchRelation {
    public PersonSearchRelation() {}
    public PersonSearchRelation(String id, SMDEntity reference) {
        super(id,reference);
    }
}
