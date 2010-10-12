package org.socialmusicdiscovery.server.business.model.subjective;

import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.SMDEntityReference;

import javax.persistence.*;
import java.io.Serializable;

public class RelationPK implements Serializable {
    private String type;
    @Column(name="from_id", length = 36)
    private String fromId;
    @Column(name="to_id", length = 36)
    private String toId;

    public RelationPK() {}
    public RelationPK(String type, String fromId, String toId) {
        this.type = type;
        this.fromId = fromId;
        this.toId = toId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean equals(Object object) {
        if (object instanceof RelationPK) {
            RelationPK pk = (RelationPK) object;
            return toId.equals(pk.getToId()) && fromId.equals(pk.getFromId()) && type.equals(pk.getType());
        } else {
            return false;
        }
    }

    public int hashCode() {
        int hashCode = (toId == null ? 0 : toId.hashCode());
        hashCode = 31 * hashCode + (fromId == null ? 0 : fromId.hashCode());
        hashCode = 31 * hashCode + (type == null ? 0 : type.hashCode());
        return hashCode;
    }
}
