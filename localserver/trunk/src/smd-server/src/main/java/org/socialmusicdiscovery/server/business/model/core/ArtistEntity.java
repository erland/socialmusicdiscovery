package org.socialmusicdiscovery.server.business.model.core;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.search.ArtistSearchRelationEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@javax.persistence.Entity
@Table(name = "artists")
@SMDIdentityReferenceEntity.ReferenceType(type = Artist.class)
public class ArtistEntity extends AbstractSMDIdentityEntity implements Artist {
    @Column(nullable = false)
    @Expose
    private String name;

    @OneToMany(targetEntity = ArtistEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "alias_artist_id")
    @Expose
    private Set<Artist> aliases;

    @ManyToOne(targetEntity = PersonEntity.class)
    @JoinColumn(name = "person_id")
    @Expose
    private Person person;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "id")
    private Set<ArtistSearchRelationEntity> searchRelations = new HashSet<ArtistSearchRelationEntity>();

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

    public Set<ArtistSearchRelationEntity> getSearchRelations() {
        return searchRelations;
    }

    public void setSearchRelations(Set<ArtistSearchRelationEntity> searchRelations) {
        this.searchRelations = searchRelations;
    }
}
