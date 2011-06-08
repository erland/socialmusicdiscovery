package org.socialmusicdiscovery.server.plugins.mediaimport.spotify;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;

/**
 * Represents a Spotify album
 */
public class SpotifyAlbum implements SMDIdentity {
    @Expose
    private String id;
    @Expose
    private String name;

    public SpotifyAlbum() {
    }

    public SpotifyAlbum(String id, String name) {
        this.id = id;
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
}
