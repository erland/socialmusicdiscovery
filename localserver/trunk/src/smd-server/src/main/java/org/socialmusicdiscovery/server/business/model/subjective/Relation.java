package org.socialmusicdiscovery.server.business.model.subjective;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name="relations")
public class Relation extends SMDEntity<Relation> {
    //TODO: How do we annotate this to make it fit all entities ?
    private SMDEntity from;
    //TODO: How do we annotate this to make it fit all entities ?
    private SMDEntity to;

    public SMDEntity getFrom() {
        return from;
    }

    public void setFrom(SMDEntity from) {
        this.from = from;
    }

    public SMDEntity getTo() {
        return to;
    }

    public void setTo(SMDEntity to) {
        this.to = to;
    }
}
