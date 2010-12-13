package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "artists_search_relations")
public class ArtistSearchRelationEntity extends SearchRelationEntity {
    public ArtistSearchRelationEntity() {
    }

    public ArtistSearchRelationEntity(String id, SMDIdentity reference) {
        super(id, reference);
    }

    public ArtistSearchRelationEntity(String id, String referenceType, String reference) {
        super(id, referenceType, reference);
    }
}
