package org.socialmusicdiscovery.server.business.model.classification;

import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.SMDEntityReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@javax.persistence.Entity
@Table(name = "classifications")
public class Classification extends SMDEntity {
    public static final String GENRE = "genre";
    public static final String MOOD = "mood";
    public static final String STYLE = "style";
    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private String name;
    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "parent_id")
    private Set<Classification> childs = new HashSet<Classification>();

    @ManyToMany
    @JoinTable(name = "classification_references",
            joinColumns = @JoinColumn(name = "classification_id"),
            inverseJoinColumns = @JoinColumn(name = "reference_id"))
    private Set<SMDEntityReference> references = new HashSet<SMDEntityReference>();

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

    public Set<Classification> getChilds() {
        return childs;
    }

    public void setChilds(Set<Classification> childs) {
        this.childs = childs;
    }

    public Set<SMDEntityReference> getReferences() {
        return references;
    }

    public void setReferences(Set<SMDEntityReference> references) {
        this.references = references;
    }
}
