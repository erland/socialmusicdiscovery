package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "works_search_relations")
public class WorkSearchRelation extends SearchRelation {
    public WorkSearchRelation() {}
    public WorkSearchRelation(String id, SMDEntity reference) {
        super(id,reference);
    }
}
