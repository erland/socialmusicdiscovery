package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "artists_search_relations")
public class ArtistSearchRelation extends SearchRelation {
    public ArtistSearchRelation() {}
    public ArtistSearchRelation(String id, SMDEntity reference) {
        super(id,reference);
    }
}
