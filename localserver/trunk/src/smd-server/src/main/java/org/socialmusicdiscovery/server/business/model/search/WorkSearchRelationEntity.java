package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.core.Work;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "works_search_relations")
public class WorkSearchRelationEntity extends SearchRelationEntity {
    public WorkSearchRelationEntity() {
    }

    public WorkSearchRelationEntity(Work work, SMDIdentity reference) {
        super(work, reference);
    }

    public WorkSearchRelationEntity(Work work, Classification classification) {
        super(work, classification);
    }
}
