package org.socialmusicdiscovery.server.business.model.core;

import jo4neo.neo;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class Release extends SMDEntity<Release> {
    @neo
    private Date date;
    @neo(index=true)
    private String name;
    @neo
    private Label label;
    @neo
    private Collection<Medium> mediums;
    @neo
    private Collection<Track> tracks;
    @neo
    private Collection<RecordingSession> recordingSessions;
    @neo
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
