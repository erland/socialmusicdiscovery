package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import java.util.Set;

public interface Artist extends SMDIdentity {
    String getName();

    void setName(String name);

    Person getPerson();

    void setPerson(Person person);

    Set<Artist> getAliases();

    void setAliases(Set<Artist> aliases);
}
