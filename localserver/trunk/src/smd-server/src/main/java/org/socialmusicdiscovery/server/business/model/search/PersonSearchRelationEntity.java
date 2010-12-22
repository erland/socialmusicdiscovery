package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "persons_search_relations")
public class PersonSearchRelationEntity extends SearchRelationEntity {
    public PersonSearchRelationEntity() {}
    public PersonSearchRelationEntity(String id, SMDIdentity reference) {
        super(id,reference);
    }
    public PersonSearchRelationEntity(String id, String referenceType, String reference, String type) {
        super(id,referenceType,reference,type);
    }
}
