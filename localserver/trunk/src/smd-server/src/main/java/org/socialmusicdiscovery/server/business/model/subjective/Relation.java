package org.socialmusicdiscovery.server.business.model.subjective;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@javax.persistence.Entity
@Table(name = "relations")
@IdClass(Relation.class)
public class Relation implements Serializable {
    @Id
    private String type;

    @Id
    @Column(name = "from_id", length = 36)
    private String fromId;

    @Id
    @Column(name = "to_id", length = 36)
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
