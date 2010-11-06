package org.socialmusicdiscovery.server.business.model.core;

import org.hibernate.annotations.Index;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "label")
@javax.persistence.Entity
@Table(name = "labels")
public class Label extends SMDEntity<Label> {
    @Column(nullable = false)
    @Index(name ="nameIndex")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
