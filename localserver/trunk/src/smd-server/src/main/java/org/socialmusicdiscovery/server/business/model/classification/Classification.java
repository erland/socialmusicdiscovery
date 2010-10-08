package org.socialmusicdiscovery.server.business.model.classification;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collection;

@javax.persistence.Entity
@Table(name="classifications")
public class Classification extends SMDEntity {
    private String type;
    private String name;
    @OneToMany
    @JoinColumn(name="parent_id")
    private Collection<Classification> childs;
    //TODO: How do we annotate this ?
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
