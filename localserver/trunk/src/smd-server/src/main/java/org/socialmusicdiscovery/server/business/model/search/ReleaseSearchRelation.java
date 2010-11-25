package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "releases_search_relations")
public class ReleaseSearchRelation extends SearchRelation {
    public ReleaseSearchRelation() {}
    public ReleaseSearchRelation(String id, SMDEntity reference) {
        super(id,reference);
    }
}
