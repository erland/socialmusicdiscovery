package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.*;
import java.util.Collection;

@javax.persistence.Entity
@Table(name = "artists")
public class Artist extends SMDEntity<Artist> {
    private String name;

    @OneToMany
    @JoinColumn(name="alias_person_id")
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
