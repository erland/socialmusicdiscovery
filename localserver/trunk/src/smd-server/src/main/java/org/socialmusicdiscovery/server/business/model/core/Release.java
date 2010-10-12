package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "release")
@javax.persistence.Entity
@Table(name = "releases")
public class Release extends SMDEntity<Release> {
    private Date date;
    @Column(nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name="label_id")
    private Label label;
    @OneToMany
    @JoinColumn(name = "release_id", nullable = false)
    private Collection<Medium> mediums;
    @OneToMany
    @JoinColumn(name = "release_id")
    private Collection<Track> tracks;
    @ManyToMany
    @JoinTable(name="release_recording_sessions",
          joinColumns=@JoinColumn(name="release_id"),
          inverseJoinColumns=@JoinColumn(name="session_id"))
    private Collection<RecordingSession> recordingSessions;
    @OneToMany
    @JoinColumn(name = "release_id")
    private Collection<Contributor> contributors = new ArrayList<Contributor>();

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

    public Collection<Medium> getMediums() {
        return mediums;
    }

    public void setMediums(Collection<Medium> mediums) {
        this.mediums = mediums;
    }

    public Collection<Track> getTracks() {
        return tracks;
    }

    public void setTracks(Collection<Track> tracks) {
        this.tracks = tracks;
    }
    public Collection<RecordingSession> getRecordingSessions() {
        return recordingSessions;
    }

    public void setRecordingSessions(Collection<RecordingSession> recordingSessions) {
        this.recordingSessions = recordingSessions;
    }

    public Collection<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(Collection<Contributor> contributors) {
        this.contributors = contributors;
    }
}
