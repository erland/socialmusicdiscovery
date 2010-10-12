package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "artist")
@javax.persistence.Entity
@Table(name = "artists")
public class Artist extends SMDEntity<Artist> {
    @Column(nullable = false)
    private String name;

    @OneToMany
    @JoinColumn(name="alias_artist_id")
    private Collection<Artist> aliases;

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
}
