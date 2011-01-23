package org.socialmusicdiscovery.server.business.model.core;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@javax.persistence.Entity
@Table(name = "recording_sessions")
@SMDIdentityReferenceEntity.ReferenceType(type = RecordingSession.class)
public class RecordingSessionEntity extends AbstractSMDIdentityEntity implements RecordingSession {
    @Expose
    private Date date;
    @OneToMany(targetEntity = ContributorEntity.class, cascade = {CascadeType.ALL})
    @JoinColumn(name = "session_id")
    @Expose
    private Set<Contributor> contributors = new HashSet<Contributor>();

    @OneToMany(targetEntity = RecordingEntity.class, orphanRemoval = true)
    @JoinColumn(name = "session_id")
    @Expose
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
