package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

public interface PlayableElement extends SMDIdentity {
    String getSmdID();

    void setSmdID(String smdID);

    String getUri();

    void setUri(String uri);

    String getFormat();

    void setFormat(String format);

    Integer getBitrate();

    void setBitrate(Integer bitrate);
}
