package org.socialmusicdiscovery.server.business.model.core;

import jo4neo.neo;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

import java.util.Collection;
import java.util.Date;

public class Recording extends SMDEntity<Recording> {
    @neo
    private String name;
    @neo
    private Date date;
    @neo
    private Recording mixOf;
    @neo
    private Collection<Contributor> contributors;
    @neo
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
