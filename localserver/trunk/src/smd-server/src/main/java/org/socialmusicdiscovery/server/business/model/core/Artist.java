package org.socialmusicdiscovery.server.business.model.core;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Index;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "artist")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@javax.persistence.Entity
@Table(name = "artists")
public class Artist extends SMDEntity<Artist> {
    @Column(nullable = false)
    @Index(name ="nameIndex")
    private String name;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="alias_artist_id")
    private Set<Artist> aliases;

    @ManyToOne
    @JoinColumn(name="person_id")
    private Person person;

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
}
