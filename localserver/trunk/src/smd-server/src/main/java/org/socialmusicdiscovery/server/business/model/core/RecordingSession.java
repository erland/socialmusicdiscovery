package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@javax.persistence.Entity
@Table(name = "recording_sessions")
public class RecordingSession extends SMDEntity<RecordingSession> {
    private Date date;
    @OneToMany
    @JoinColumn(name= "session_id")
    private Collection<Contributor> contributors;
    @ManyToOne
    @JoinColumn(name="work_id")
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
