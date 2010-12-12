package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "track")
@javax.persistence.Entity
@Table(name = "tracks")
public class Track extends SMDEntity {
    private Integer number;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recording_id")
    private Recording recording;

    @OneToMany
    @JoinColumn(name = "track_id")
    private Set<PlayableElement> playableElements = new HashSet<PlayableElement>();

    @ManyToOne
    @JoinColumn(name = "medium_id")
    private Medium medium;

    @ManyToOne
    @JoinColumn(name = "release_id")
    @XmlTransient
    private Release release;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Medium getMedium() {
        return medium;
    }

    public void setMedium(Medium medium) {
        this.medium = medium;
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

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }
}
