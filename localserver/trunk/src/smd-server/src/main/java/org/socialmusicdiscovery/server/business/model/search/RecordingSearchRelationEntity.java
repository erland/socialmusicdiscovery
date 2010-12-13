package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "recordings_search_relations")
public class RecordingSearchRelationEntity extends SearchRelationEntity {
    public RecordingSearchRelationEntity() {}
    public RecordingSearchRelationEntity(String id, SMDIdentity reference) {
        super(id,reference);
    }
    public RecordingSearchRelationEntity(String id, String referenceType, String reference) {
        super(id, referenceType, reference);
    }
}
