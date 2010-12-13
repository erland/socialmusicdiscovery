package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

public interface Person extends SMDIdentity {
    String getName();

    void setName(String name);
}
