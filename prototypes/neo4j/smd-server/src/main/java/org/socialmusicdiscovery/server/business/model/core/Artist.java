package org.socialmusicdiscovery.server.business.model.core;

import jo4neo.neo;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

import java.util.Collection;

public class Artist extends SMDEntity<Artist> {
    @neo(index=true)
    private String name;

    @neo
    private Collection<Artist> aliases;

    @neo
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
