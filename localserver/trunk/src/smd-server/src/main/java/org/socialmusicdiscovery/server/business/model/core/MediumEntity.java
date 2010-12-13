package org.socialmusicdiscovery.server.business.model.core;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;

import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@javax.persistence.Entity
@Table(name = "mediums")
@SMDIdentityReferenceEntity.ReferenceType(type = Medium.class)
public class MediumEntity extends AbstractSMDIdentityEntity implements Medium {
    @Expose
    private Integer number;
    @Expose
    private String name;
    @OneToMany(targetEntity = TrackEntity.class)
    @JoinColumn(name = "medium_id")
    @OrderBy("number")
    private List<Track> tracks = new ArrayList<Track>();

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
}
