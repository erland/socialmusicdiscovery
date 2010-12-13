package org.socialmusicdiscovery.server.business.model.core;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.search.RecordingSearchRelationEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@javax.persistence.Entity
@Table(name = "recordings")
@SMDIdentityReferenceEntity.ReferenceType(type = Recording.class)
public class RecordingEntity extends AbstractSMDIdentityEntity implements Recording {
    @Expose
    private String name;
    @Expose
    private Date date;
    @ManyToOne(targetEntity = RecordingEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "mixof_id")
    @Expose
    private Recording mixOf;
    @OneToMany(targetEntity = ContributorEntity.class)
    @JoinColumn(name = "recording_id")
    @Expose
    private Set<Contributor> contributors = new HashSet<Contributor>();
    @ManyToOne(targetEntity = WorkEntity.class, optional = false)
    @JoinColumn(name = "work_id")
    @Expose
    private Work work;

    @ManyToOne(targetEntity = RecordingSessionEntity.class)
    @JoinColumn(name = "session_id")
    private RecordingSession recordingSession;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "id")
    private Set<RecordingSearchRelationEntity> searchRelations;

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

    public Set<RecordingSearchRelationEntity> getSearchRelations() {
        return searchRelations;
    }

    public void setSearchRelations(Set<RecordingSearchRelationEntity> searchRelations) {
        this.searchRelations = searchRelations;
    }

    public RecordingSession getRecordingSession() {
        return recordingSession;
    }

    public void setRecordingSession(RecordingSession recordingSession) {
        this.recordingSession = recordingSession;
    }
}
