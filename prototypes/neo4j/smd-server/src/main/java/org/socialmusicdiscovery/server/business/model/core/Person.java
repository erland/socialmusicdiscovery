package org.socialmusicdiscovery.server.business.model.core;

import jo4neo.neo;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

public class Person extends SMDEntity<Person> {
    @neo(index=true)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
