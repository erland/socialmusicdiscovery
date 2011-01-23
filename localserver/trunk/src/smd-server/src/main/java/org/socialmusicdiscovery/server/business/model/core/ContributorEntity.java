package org.socialmusicdiscovery.server.business.model.core;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "contributors")
@SMDIdentityReferenceEntity.ReferenceType(type = Contributor.class)
public class ContributorEntity extends AbstractSMDIdentityEntity implements Contributor {
    @Column(nullable = false)
    @Expose
    private String type;
    @ManyToOne(optional = false, targetEntity = ArtistEntity.class)
    @JoinColumn(name = "artist_id")
    @Expose
    private Artist artist;

    public ContributorEntity() {}
    public ContributorEntity(Artist artist, String type) {
        setArtist(artist);
        setType(type);
    }

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
