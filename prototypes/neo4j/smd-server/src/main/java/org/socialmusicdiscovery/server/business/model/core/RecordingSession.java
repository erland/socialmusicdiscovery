package org.socialmusicdiscovery.server.business.model.core;

import jo4neo.neo;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

import java.util.Collection;
import java.util.Date;

public class RecordingSession extends SMDEntity<RecordingSession> {
    @neo
    private Date date;
    @neo
    private Collection<Contributor> contributors;
    @neo
    private Work work;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
