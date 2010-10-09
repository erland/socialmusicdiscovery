package org.socialmusicdiscovery.server.business.model;

import jo4neo.Nodeid;

public class SMDEntity<T> {
    transient private Nodeid id;

    Long getId() {
        return id.id();
    }

}
