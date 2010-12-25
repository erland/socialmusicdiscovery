package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "classifications_search_relations")
public class ClassificationSearchRelationEntity extends SearchRelationEntity {
    public ClassificationSearchRelationEntity() {}
    public ClassificationSearchRelationEntity(String id, SMDIdentity reference) {
        super(id,reference);
    }
    public ClassificationSearchRelationEntity(String id, String referenceType, String reference, String type) {
        super(id,referenceType,reference,type);
    }
}
