package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import java.util.Set;

public interface Track extends SMDIdentity {
    Integer getNumber();

    void setNumber(Integer number);

    Medium getMedium();

    void setMedium(Medium medium);

    Recording getRecording();

    void setRecording(Recording recording);

    Set<PlayableElement> getPlayableElements();

    void setPlayableElements(Set<PlayableElement> playableElements);

    Release getRelease();

    void setRelease(Release release);
}
