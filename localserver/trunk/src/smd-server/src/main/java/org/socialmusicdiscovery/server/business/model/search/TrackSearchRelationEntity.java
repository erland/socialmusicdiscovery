package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "tracks_search_relations")
public class TrackSearchRelationEntity extends SearchRelationEntity {
    public TrackSearchRelationEntity() {}
    public TrackSearchRelationEntity(String id, SMDIdentity reference) {
        super(id,reference);
    }
}
