package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

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
@XmlRootElement(name = "recording_session")
@javax.persistence.Entity
@Table(name = "recording_sessions")
public class RecordingSession extends SMDEntity<RecordingSession> {
    private Date date;
    @OneToMany
    @JoinColumn(name = "session_id")
    private Set<Contributor> contributors = new HashSet<Contributor>();

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "session_id")
    private Set<Recording> recordings = new HashSet<Recording>();

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(Set<Contributor> contributors) {
        this.contributors = contributors;
    }

    public Set<Recording> getRecordings() {
        return recordings;
    }

    public void setRecordings(Set<Recording> recordings) {
        this.recordings = recordings;
    }
}
