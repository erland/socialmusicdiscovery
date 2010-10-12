package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "track")
@javax.persistence.Entity
@Table(name = "tracks")
public class Track extends SMDEntity<Track> {
    private Integer number;
    @ManyToOne
    @JoinColumn(name="recording_id")
    private Recording recording;

    @OneToMany
    @JoinColumn(name="track_id", nullable = false)
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
