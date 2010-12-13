package org.socialmusicdiscovery.server.business.model;

public interface GlobalIdentity {
    final static String SOURCE_MUSICBRAINZ = "musicbrainz";

    String getId();

    void setId(String id);

    String getSource();

    void setSource(String source);

    String getUri();

    void setUri(String uri);

    String getEntityId();

    void setEntityId(String entityId);
}
