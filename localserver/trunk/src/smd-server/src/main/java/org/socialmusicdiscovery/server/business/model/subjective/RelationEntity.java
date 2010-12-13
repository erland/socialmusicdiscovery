package org.socialmusicdiscovery.server.business.model.subjective;

import com.google.gson.annotations.Expose;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@javax.persistence.Entity
@Table(name = "relations")
@IdClass(RelationEntity.class)
public class RelationEntity implements Serializable, Relation {
    @Id
    @Expose
    private String type;

    @Id
    @Column(name = "from_id", length = 36)
    @Expose
    private String fromId;

    @Id
    @Column(name = "to_id", length = 36)
    @Expose
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
