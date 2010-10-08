package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.*;

@javax.persistence.Entity
@Table(name = "persons")
public class Person extends SMDEntity<Person> {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
