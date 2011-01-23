package org.socialmusicdiscovery.server.business.model.core;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@javax.persistence.Entity
@Table(name = "works")
@SMDIdentityReferenceEntity.ReferenceType(type = Work.class)
public class WorkEntity extends AbstractSMDIdentityEntity implements Work {
    @Column(nullable = false)
    @Expose
    private String name;
    @Expose
    private Date date;
    @OneToMany(targetEntity = WorkEntity.class)
    @JoinColumn(name = "parent_id")
    private Set<Work> parts = new HashSet<Work>();
    @ManyToOne(targetEntity = WorkEntity.class)
    @JoinColumn(name = "parent_id")
    @Expose
    private Work parent;

    @OneToMany(targetEntity = ContributorEntity.class, cascade = {CascadeType.ALL})
    @JoinColumn(name = "work_id")
    @Expose
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
}
