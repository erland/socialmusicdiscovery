package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.*;
import java.util.Collection;

@javax.persistence.Entity
@Table(name = "tracks")
public class Track extends SMDEntity<Track> {
    private Integer number;
    @ManyToOne
    @JoinColumn(name="recording_id")
    private Recording recording;

    @OneToMany
    @JoinColumn(name="track_id")
    private Collection<PlayableElement> playableElements;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Recording getRecording() {
        return recording;
    }

    public void setRecording(Recording recording) {
        this.recording = recording;
    }

    public Collection<PlayableElement> getPlayableElements() {
        return playableElements;
    }

    public void setPlayableElements(Collection<PlayableElement> playableElements) {
        this.playableElements = playableElements;
    }
}
