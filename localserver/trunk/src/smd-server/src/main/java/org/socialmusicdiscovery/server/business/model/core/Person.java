package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.search.PersonSearchRelation;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "person")
@javax.persistence.Entity
@Table(name = "persons")
public class Person extends SMDEntity<Person> {
    @Column(nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="id")
    @XmlTransient
    private Set<PersonSearchRelation> searchRelations = new HashSet<PersonSearchRelation>();;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<PersonSearchRelation> getSearchRelations() {
        return searchRelations;
    }

    public void setSearchRelations(Set<PersonSearchRelation> searchRelations) {
        this.searchRelations = searchRelations;
    }
}
