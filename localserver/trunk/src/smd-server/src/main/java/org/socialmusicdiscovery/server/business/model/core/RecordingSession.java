package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "recording_session")
@javax.persistence.Entity
@Table(name = "recording_sessions")
public class RecordingSession extends SMDEntity<RecordingSession> {
    private Date date;
    @OneToMany
    @JoinColumn(name= "session_id")
    private Collection<Contributor> contributors;

    @OneToMany
    @JoinColumn(name="session_id")
    private Collection<Recording> recordings;
    
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

    public Collection<Recording> getRecordings() {
        return recordings;
    }

    public void setRecordings(Collection<Recording> recordings) {
        this.recordings = recordings;
    }
}
