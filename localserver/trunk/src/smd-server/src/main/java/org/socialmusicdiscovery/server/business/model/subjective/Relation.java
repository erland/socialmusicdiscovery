package org.socialmusicdiscovery.server.business.model.subjective;

public interface Relation {
    String getType();

    void setType(String type);

    String getFromId();

    void setFromId(String fromId);

    String getToId();

    void setToId(String toId);
}
