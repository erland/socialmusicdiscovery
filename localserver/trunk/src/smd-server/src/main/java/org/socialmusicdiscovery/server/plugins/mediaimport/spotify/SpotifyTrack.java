package org.socialmusicdiscovery.server.plugins.mediaimport.spotify;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.core.PlayableElement;

import java.util.HashSet;
import java.util.Set;

/**
 * Represent a Spotify track
 */
public class SpotifyTrack implements SMDIdentity {
    @Expose
    private String id;
    @Expose
    private Integer number;
    @Expose
    private String name;
    @Expose
    private Set<PlayableElement> playableElements = new HashSet<PlayableElement>();

    public SpotifyTrack() {
    }

    public SpotifyTrack(String id, Integer number, String name) {
        this.id = id;
        this.number = number;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Set<PlayableElement> getPlayableElements() {
        return playableElements;
    }

    public void setPlayableElements(Set<PlayableElement> playableElements) {
        this.playableElements = playableElements;
    }
}
