package org.socialmusicdiscovery.server.business.model.subjective;

import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.core.Person;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@javax.persistence.Entity
@Table(name="credits")
public class Credit extends SMDEntity<Credit> {
    private String type;
    @ManyToOne
    @JoinColumn(name="person_id")
    private Person person;
    //TODO: How do we annotate this ?
    private SMDEntity entity;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
