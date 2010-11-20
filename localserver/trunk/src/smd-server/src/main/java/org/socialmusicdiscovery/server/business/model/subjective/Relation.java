package org.socialmusicdiscovery.server.business.model.subjective;

import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "relations")
@IdClass(RelationPK.class)
public class Relation {
    @Id
    private String type;

    @Id
    private String fromId;

    @Id
    private String toId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }
}
