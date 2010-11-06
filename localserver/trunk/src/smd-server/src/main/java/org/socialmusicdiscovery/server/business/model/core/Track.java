package org.socialmusicdiscovery.server.business.model.core;

import org.hibernate.annotations.Index;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "track")
@javax.persistence.Entity
@Table(name = "tracks")
public class Track extends SMDEntity<Track> {
    @Index(name ="numberIndex")
    private Integer number;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="recording_id")
    private Recording recording;

    @OneToMany
    @JoinColumn(name="track_id")
    private Set<PlayableElement> playableElements = new HashSet<PlayableElement>();

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

    public Set<PlayableElement> getPlayableElements() {
        return playableElements;
    }

    public void setPlayableElements(Set<PlayableElement> playableElements) {
        this.playableElements = playableElements;
    }
}
