package org.socialmusicdiscovery.server.plugins.mediaimport.lastfm;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;

/**
 * Represent a LastFM track
 */
public class LastFMTrack implements SMDIdentity {
    @Expose
    private String id;
    @Expose
    private Integer number;
    @Expose
    private String name;

    public LastFMTrack() {
    }

    public LastFMTrack(String id, Integer number, String name) {
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
}
