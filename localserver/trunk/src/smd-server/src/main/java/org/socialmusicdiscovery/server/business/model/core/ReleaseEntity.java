package org.socialmusicdiscovery.server.business.model.core;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.search.ReleaseSearchRelationEntity;

import javax.persistence.*;
import java.util.*;

@javax.persistence.Entity
@Table(name = "releases")
@SMDIdentityReferenceEntity.ReferenceType(type = Release.class)
public class ReleaseEntity extends AbstractSMDIdentityEntity implements Release {
    private Date date;
    @Column(nullable = false)
    @Expose
    private String name;
    @ManyToOne(targetEntity = LabelEntity.class)
    @JoinColumn(name = "label_id")
    @Expose
    private Label label;
    @OneToMany(targetEntity = MediumEntity.class)
    @OrderBy("number, name")
    @JoinColumn(name = "release_id", nullable = false)
    @Expose
    private List<Medium> mediums = new ArrayList<Medium>();
    @OneToMany(targetEntity = TrackEntity.class)
    @JoinColumn(name = "release_id")
    @OrderBy("number")
    @Expose
    private List<Track> tracks = new ArrayList<Track>();
    @ManyToMany(targetEntity = RecordingSessionEntity.class)
    @JoinTable(name = "release_recording_sessions",
            joinColumns = @JoinColumn(name = "release_id"),
            inverseJoinColumns = @JoinColumn(name = "session_id"))
    @Expose
    private Set<RecordingSession> recordingSessions = new HashSet<RecordingSession>();
    @OneToMany(targetEntity = ContributorEntity.class)
    @JoinColumn(name = "release_id")
    @Expose
    private Set<Contributor> contributors = new HashSet<Contributor>();

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "id")
    private Set<ReleaseSearchRelationEntity> searchRelations = new HashSet<ReleaseSearchRelationEntity>();

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public List<Medium> getMediums() {
        return mediums;
    }

    public void setMediums(List<Medium> mediums) {
        this.mediums = mediums;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public Set<RecordingSession> getRecordingSessions() {
        return recordingSessions;
    }

    public void setRecordingSessions(Set<RecordingSession> recordingSessions) {
        this.recordingSessions = recordingSessions;
    }

    public Set<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(Set<Contributor> contributors) {
        this.contributors = contributors;
    }

    public Set<ReleaseSearchRelationEntity> getSearchRelations() {
        return searchRelations;
    }

    public void setSearchRelations(Set<ReleaseSearchRelationEntity> searchRelations) {
        this.searchRelations = searchRelations;
    }
}
