package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "recordings_search_relations")
public class RecordingSearchRelation extends SearchRelation {
    public RecordingSearchRelation() {}
    public RecordingSearchRelation(String id, SMDEntity reference) {
        super(id,reference);
    }
    public RecordingSearchRelation(String id, String referenceType, String reference) {
        super(id, referenceType, reference);
    }
}
