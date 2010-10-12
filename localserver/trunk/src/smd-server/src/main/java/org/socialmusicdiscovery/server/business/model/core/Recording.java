package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "recording")
@javax.persistence.Entity
@Table(name = "recordings")
public class Recording extends SMDEntity<Recording> {
    private String name;
    private Date date;
    @ManyToOne
    @JoinColumn(name="mixof_id")
    private Recording mixOf;
    @OneToMany
    @JoinColumn(name="recording_id")
    private Collection<Contributor> contributors;
    @ManyToOne(optional = false)
    @JoinColumn(name="work_id")
    private Work work;

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

    public Recording getMixOf() {
        return mixOf;
    }

    public void setMixOf(Recording mixOf) {
        this.mixOf = mixOf;
    }

    public Collection<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(Collection<Contributor> contributors) {
        this.contributors = contributors;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }
}
