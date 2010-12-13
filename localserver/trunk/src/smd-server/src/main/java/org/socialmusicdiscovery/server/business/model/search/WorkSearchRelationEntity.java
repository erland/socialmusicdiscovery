package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "works_search_relations")
public class WorkSearchRelationEntity extends SearchRelationEntity {
    public WorkSearchRelationEntity() {}
    public WorkSearchRelationEntity(String id, SMDIdentity reference) {
        super(id,reference);
    }
}
