package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

public interface Label extends SMDIdentity {
    String getName();

    void setName(String name);
}
