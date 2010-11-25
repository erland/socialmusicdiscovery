package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.search.WorkSearchRelation;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
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
    @XmlTransient  // XmlTransient required to avoid circular dependencies during JSON/XML encoding
    private Set<Work> parts = new HashSet<Work>();
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Work parent;

    @OneToMany
    @JoinColumn(name = "work_id")
    private Set<Contributor> contributors = new HashSet<Contributor>();

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "id")
    @XmlTransient
    private Set<WorkSearchRelation> searchRelations;

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

    public Work getParent() {
        return parent;
    }

    public void setParent(Work parent) {
        this.parent = parent;
    }

    public Set<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(Set<Contributor> contributors) {
        this.contributors = contributors;
    }

    public Set<WorkSearchRelation> getSearchRelations() {
        return searchRelations;
    }

    public void setSearchRelations(Set<WorkSearchRelation> searchRelations) {
        this.searchRelations = searchRelations;
    }
}
