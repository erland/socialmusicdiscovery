package org.socialmusicdiscovery.server.business.model.core;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.search.ArtistSearchRelation;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "artist")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@javax.persistence.Entity
@Table(name = "artists")
public class Artist extends SMDEntity {
    @Column(nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "alias_artist_id")
    private Set<Artist> aliases;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "id")
    @XmlTransient
    private Set<ArtistSearchRelation> searchRelations = new HashSet<ArtistSearchRelation>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Set<Artist> getAliases() {
        return aliases;
    }

    public void setAliases(Set<Artist> aliases) {
        this.aliases = aliases;
    }

    public Set<ArtistSearchRelation> getSearchRelations() {
        return searchRelations;
    }

    public void setSearchRelations(Set<ArtistSearchRelation> searchRelations) {
        this.searchRelations = searchRelations;
    }
}
