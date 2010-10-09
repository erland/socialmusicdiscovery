package org.socialmusicdiscovery.server.business.model.classification;

import jo4neo.neo;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

import java.util.Collection;

public class Classification extends SMDEntity {
    @neo
    private String type;
    @neo(index=true)
    private String name;
    @neo
    private Collection<Classification> childs;
    //TODO: How do we annotate this ?
    @neo
    private SMDEntity entity;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Classification> getChilds() {
        return childs;
    }

    public void setChilds(Collection<Classification> childs) {
        this.childs = childs;
    }
}
