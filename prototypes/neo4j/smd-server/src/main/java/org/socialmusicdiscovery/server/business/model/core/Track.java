package org.socialmusicdiscovery.server.business.model.core;

import jo4neo.neo;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

import java.util.Collection;

public class Track extends SMDEntity<Track> {
    @neo
    private Integer number;
    @neo
    private Recording recording;
    @neo
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
