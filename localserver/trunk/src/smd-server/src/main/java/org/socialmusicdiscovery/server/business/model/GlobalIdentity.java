package org.socialmusicdiscovery.server.business.model;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name="global_entities")
public class GlobalIdentity extends SMDEntity {
    private String source;
    private String identity;
    //TODO: How do with annotate this to make it generic enough ?
    private SMDEntity entity;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public SMDEntity getEntity() {
        return entity;
    }

    public void setEntity(SMDEntity entity) {
        this.entity = entity;
    }
}
