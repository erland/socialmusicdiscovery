package org.socialmusicdiscovery.server.business.model.core;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@javax.persistence.Entity
@Table(name = "tracks")
@SMDIdentityReferenceEntity.ReferenceType(type = Track.class)
public class TrackEntity extends AbstractSMDIdentityEntity implements Track {
    @Expose
    private Integer number;
    @ManyToOne(targetEntity = RecordingEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "recording_id")
    @Expose
    private Recording recording;

    @OneToMany(targetEntity = PlayableElementEntity.class)
    @JoinColumn(name = "track_id")
    @Expose
    private Set<PlayableElement> playableElements = new HashSet<PlayableElement>();

    @ManyToOne(targetEntity = MediumEntity.class)
    @JoinColumn(name = "medium_id")
    @Expose
    private Medium medium;

    @ManyToOne(targetEntity = ReleaseEntity.class)
    @JoinColumn(name = "release_id")
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
