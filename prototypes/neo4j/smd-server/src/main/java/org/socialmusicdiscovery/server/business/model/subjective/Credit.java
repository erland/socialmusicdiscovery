package org.socialmusicdiscovery.server.business.model.subjective;

import jo4neo.neo;
import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.core.Person;

public class Credit extends SMDEntity<Credit> {
    @neo
    private String type;
    @neo
    private Person person;
    //TODO: How do we annotate this ?
    @neo
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
