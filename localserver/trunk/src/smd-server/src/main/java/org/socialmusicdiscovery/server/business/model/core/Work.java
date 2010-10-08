package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@javax.persistence.Entity
@Table(name = "works")
public class Work extends SMDEntity<Work> {
    private String name;
    private Date date;
    @OneToMany
    @JoinColumn(name="parent_id")
    private Collection<Work> parts;
    @OneToMany
    @JoinColumn(name="work_id")
    private Collection<Contributor> contributors;

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

    public Collection<Work> getParts() {
        return parts;
    }

    public void setParts(Collection<Work> parts) {
        this.parts = parts;
    }

    public Collection<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(Collection<Contributor> contributors) {
        this.contributors = contributors;
    }
}
