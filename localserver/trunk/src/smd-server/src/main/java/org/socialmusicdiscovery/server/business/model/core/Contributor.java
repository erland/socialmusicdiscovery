package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "contributors")
public class Contributor extends SMDEntity<Contributor> {
    private String type;
    @ManyToOne
    @JoinColumn(name="artist_id")
    private Artist artist;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
