package org.socialmusicdiscovery.server.business.model.classification;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReference;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.search.ClassificationSearchRelationEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@javax.persistence.Entity
@Table(name = "classifications")
@SMDIdentityReferenceEntity.ReferenceType(type = Classification.class)
public class ClassificationEntity extends AbstractSMDIdentityEntity implements Classification {
    @Column(nullable = false)
    @Expose
    private String type;
    @Column(nullable = false)
    @Expose
    private String name;
    @OneToMany(targetEntity = ClassificationEntity.class, orphanRemoval = true)
    @JoinColumn(name = "parent_id")
    @Expose
    private Set<Classification> childs = new HashSet<Classification>();

    @ManyToMany(targetEntity = SMDIdentityReferenceEntity.class)
    @JoinTable(name = "classification_references",
            joinColumns = @JoinColumn(name = "classification_id"),
            inverseJoinColumns = @JoinColumn(name = "reference_id"))
    private Set<SMDIdentityReference> references = new HashSet<SMDIdentityReference>();

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "id")
    private Set<ClassificationSearchRelationEntity> searchRelations;

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

    public Set<SMDIdentityReference> getReferences() {
        return references;
    }

    public void setReferences(Set<SMDIdentityReference> references) {
        this.references = references;
    }

    public Set<ClassificationSearchRelationEntity> getSearchRelations() {
        return searchRelations;
    }

    public void setSearchRelations(Set<ClassificationSearchRelationEntity> searchRelations) {
        this.searchRelations = searchRelations;
    }
}
