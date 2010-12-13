package org.socialmusicdiscovery.server.business.model.core;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.search.PersonSearchRelationEntity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@javax.persistence.Entity
@Table(name = "persons")
@SMDIdentityReferenceEntity.ReferenceType(type = Person.class)
public class PersonEntity extends AbstractSMDIdentityEntity implements Person {
    @Column(nullable = false)
    @Expose
    private String name;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "id")
    private Set<PersonSearchRelationEntity> searchRelations = new HashSet<PersonSearchRelationEntity>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<PersonSearchRelationEntity> getSearchRelations() {
        return searchRelations;
    }

    public void setSearchRelations(Set<PersonSearchRelationEntity> searchRelations) {
        this.searchRelations = searchRelations;
    }
}
