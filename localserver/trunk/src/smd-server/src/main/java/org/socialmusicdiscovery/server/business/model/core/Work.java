package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "work")
@javax.persistence.Entity
@Table(name = "works")
public class Work extends SMDEntity<Work> {
    @Column(nullable = false)
    private String name;
    private Date date;
    @OneToMany
    @JoinColumn(name = "parent_id")
    private Set<Work> parts = new HashSet<Work>();
    @OneToMany
    @JoinColumn(name = "work_id")
    private Set<Contributor> contributors = new HashSet<Contributor>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<Work> getParts() {
        return parts;
    }

    public void setParts(Set<Work> parts) {
        this.parts = parts;
    }

    public Set<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(Set<Contributor> contributors) {
        this.contributors = contributors;
    }
}
