package org.socialmusicdiscovery.server.business.model.classification;

import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.SMDEntityReference;

import javax.persistence.*;
import java.util.Collection;

@javax.persistence.Entity
@Table(name="classifications")
public class Classification extends SMDEntity {
    @Column(nullable = false)
    private String type;
    private String name;
    @OneToMany
    @JoinColumn(name="parent_id")
    private Collection<Classification> childs;

    @OneToMany
    @JoinTable(name="classification_references",
          joinColumns=@JoinColumn(name="classification_id"),
          inverseJoinColumns=@JoinColumn(name="reference_id"))
    private Collection<SMDEntityReference> references;

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

    public Collection<SMDEntityReference> getReferences() {
        return references;
    }

    public void setReferences(Collection<SMDEntityReference> references) {
        this.references = references;
    }
}
