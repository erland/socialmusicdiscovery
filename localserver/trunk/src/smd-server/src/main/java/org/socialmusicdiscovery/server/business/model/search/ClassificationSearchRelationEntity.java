package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.core.Contributor;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "classifications_search_relations")
public class ClassificationSearchRelationEntity extends SearchRelationEntity {
    public ClassificationSearchRelationEntity() {
    }

    public ClassificationSearchRelationEntity(Classification classification, SMDIdentity reference) {
        super(classification, reference);
    }

    public ClassificationSearchRelationEntity(Classification classification, Contributor contributor) {
        super(classification, contributor);
    }
}
