package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "release")
@javax.persistence.Entity
@Table(name = "releases")
public class Release extends SMDEntity<Release> {
    private Date date;
    @Column(nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "label_id")
    private Label label;
    @OneToMany
    @OrderBy("number, name")
    @JoinColumn(name = "release_id", nullable = false)
    private List<Medium> mediums = new ArrayList<Medium>();
    @OneToMany
    @JoinColumn(name = "release_id")
    @OrderBy("number")
    private List<Track> tracks = new ArrayList<Track>();
    @ManyToMany
    @JoinTable(name = "release_recording_sessions",
            joinColumns = @JoinColumn(name = "release_id"),
            inverseJoinColumns = @JoinColumn(name = "session_id"))
    private Set<RecordingSession> recordingSessions;
    @OneToMany
    @JoinColumn(name = "release_id")
    private Set<Contributor> contributors = new HashSet<Contributor>();

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
}
