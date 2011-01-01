package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Recording;

public abstract class RecordingSearchRelationEntity extends SearchRelationEntity {
    public RecordingSearchRelationEntity() {
    }

    public RecordingSearchRelationEntity(Recording recording, SMDIdentity reference) {
        super(recording, reference);
    }

    public RecordingSearchRelationEntity(Recording recording, Contributor contributor) {
        super(recording, contributor);
    }

    public RecordingSearchRelationEntity(Recording recording, Classification classification) {
        super(recording, classification);
    }
}
