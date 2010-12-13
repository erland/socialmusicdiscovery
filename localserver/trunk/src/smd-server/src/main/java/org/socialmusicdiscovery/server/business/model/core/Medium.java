package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

public interface Medium extends SMDIdentity {
    Integer getNumber();

    void setNumber(Integer number);

    String getName();

    void setName(String name);
}
