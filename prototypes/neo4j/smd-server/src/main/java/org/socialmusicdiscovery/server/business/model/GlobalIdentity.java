package org.socialmusicdiscovery.server.business.model;

import jo4neo.neo;

public class GlobalIdentity extends SMDEntity {
    @neo
    private String source;
    @neo
    private String identity;
    //TODO: How do with annotate this to make it generic enough ?
    @neo
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
