package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Contributor;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "artists_search_relations")
public class ArtistSearchRelationEntity extends SearchRelationEntity {
    public ArtistSearchRelationEntity() {
    }

    public ArtistSearchRelationEntity(Artist artist, SMDIdentity reference) {
        super(artist, reference);
    }

    public ArtistSearchRelationEntity(Artist artist, Classification classification) {
        super(artist, classification);
    }

    public ArtistSearchRelationEntity(Artist artist, Contributor contributor) {
        super(artist, contributor);
    }
}
