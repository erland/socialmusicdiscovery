package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

public interface Contributor extends SMDIdentity {
    final static String PERFORMER = "performer";
    final static String COMPOSER = "composer";
    final static String CONDUCTOR = "conductor";

    String getType();

    void setType(String type);

    Artist getArtist();

    void setArtist(Artist artist);
}
