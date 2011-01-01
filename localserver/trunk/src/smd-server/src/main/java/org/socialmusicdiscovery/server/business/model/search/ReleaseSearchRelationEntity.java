package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.core.Release;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "releases_search_relations")
public class ReleaseSearchRelationEntity extends SearchRelationEntity {
    public ReleaseSearchRelationEntity() {
    }

    public ReleaseSearchRelationEntity(Release release, SMDIdentity reference) {
        super(release, reference);
    }

    public ReleaseSearchRelationEntity(Release release, Classification classification) {
        super(release, classification);
    }
}
