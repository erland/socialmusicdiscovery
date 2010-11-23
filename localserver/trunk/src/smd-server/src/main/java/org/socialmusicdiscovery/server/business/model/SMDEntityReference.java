package org.socialmusicdiscovery.server.business.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "reference")
@javax.persistence.Entity
@Table(name = "smdentity_references")
public class SMDEntityReference {
    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private String type;

    public SMDEntityReference() {
    }

    public SMDEntityReference(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public static SMDEntityReference forEntity(SMDEntity entity) {
        return new SMDEntityReference(entity.getId(), entity.getClass().getName());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SMDEntityReference)) return false;

        return id.equals(((SMDEntityReference) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
