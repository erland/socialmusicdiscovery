package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.core.Track;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "tracks_search_relations")
public class TrackSearchRelationEntity extends SearchRelationEntity {
    public TrackSearchRelationEntity() {
    }

    public TrackSearchRelationEntity(Track track, SMDIdentity reference) {
        super(track, reference);
    }

    public TrackSearchRelationEntity(Track track, Classification classification) {
        super(track, classification);
    }
}
