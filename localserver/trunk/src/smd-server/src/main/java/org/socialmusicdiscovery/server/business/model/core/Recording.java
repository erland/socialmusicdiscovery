package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.search.RecordingSearchRelation;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "recording")
@javax.persistence.Entity
@Table(name = "recordings")
public class Recording extends SMDEntity<Recording> {
    private String name;
    private Date date;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="mixof_id")
    private Recording mixOf;
    @OneToMany
    @JoinColumn(name="recording_id")
    private Set<Contributor> contributors = new HashSet<Contributor>();
    @ManyToOne(optional = false)
    @JoinColumn(name="work_id")
    private Work work;

    @ManyToOne
    @JoinColumn(name="session_id")
    @XmlTransient
    private RecordingSession recordingSession;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "id")
    @XmlTransient
    private Set<RecordingSearchRelation> searchRelations;

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

    public Set<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(Set<Contributor> contributors) {
        this.contributors = contributors;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public Set<RecordingSearchRelation> getSearchRelations() {
        return searchRelations;
    }

    public void setSearchRelations(Set<RecordingSearchRelation> searchRelations) {
        this.searchRelations = searchRelations;
    }

    public RecordingSession getRecordingSession() {
        return recordingSession;
    }

    public void setRecordingSession(RecordingSession recordingSession) {
        this.recordingSession = recordingSession;
    }
}
