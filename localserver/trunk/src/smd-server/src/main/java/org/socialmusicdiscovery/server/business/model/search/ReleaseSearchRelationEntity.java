package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "releases_search_relations")
public class ReleaseSearchRelationEntity extends SearchRelationEntity {
    public ReleaseSearchRelationEntity() {}
    public ReleaseSearchRelationEntity(String id, SMDIdentity reference) {
        super(id,reference);
    }
}
