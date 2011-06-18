/*
 *  Copyright 2010-2011, Social Music Discovery project
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of Social Music Discovery project nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.socialmusicdiscovery.server.business.model.core;

import com.google.gson.annotations.Expose;
import org.hibernate.Hibernate;
import org.socialmusicdiscovery.server.business.logic.SortAsHelper;
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.search.ReleaseSearchRelationEntity;

import javax.persistence.*;
import java.util.*;

/**
 * See {@link Release}
 */
@javax.persistence.Entity
@Table(name = "releases")
@SMDIdentityReferenceEntity.ReferenceType(type = Release.class)
public class ReleaseEntity extends AbstractSMDIdentityEntity implements Release, ContributorOwner {
    private Date date;
    @Column(nullable = false)
    @Expose
    private String name;
    @Column(name="sort_as", nullable = false)
    @Expose
    private String sortAs;
    @ManyToOne(targetEntity = LabelEntity.class)
    @JoinColumn(name = "label_id")
    @Expose
    private Label label;
    @OneToMany(targetEntity = MediumEntity.class, mappedBy = "release", cascade = {CascadeType.ALL})
    @OrderBy("number, name")
    @Expose
    private List<Medium> mediums = new ArrayList<Medium>();
    @OneToMany(targetEntity = TrackEntity.class, mappedBy = "release")
    @OrderBy("number")
    private List<Track> tracks = new ArrayList<Track>();
    @ManyToMany(targetEntity = RecordingSessionEntity.class)
    @JoinTable(name = "release_recording_sessions",
            joinColumns = @JoinColumn(name = "release_id"),
            inverseJoinColumns = @JoinColumn(name = "session_id"))
    @Expose
    private Set<RecordingSession> recordingSessions = new HashSet<RecordingSession>();
    @OneToMany(targetEntity = ContributorEntity.class, mappedBy = "release", cascade = {CascadeType.REMOVE}, orphanRemoval = true)
    private Set<Contributor> contributors = new HashSet<Contributor>();

    @Expose
    @ManyToOne(targetEntity = ImageEntity.class)
    @JoinColumn(name = "default_image_id")
    private Image defaultImage;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "id")
    private Set<ReleaseSearchRelationEntity> searchRelations = new HashSet<ReleaseSearchRelationEntity>();

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public List<Medium> getMediums() {
        return mediums;
    }

    public void setMediums(List<Medium> mediums) {
        this.mediums = mediums;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public Set<RecordingSession> getRecordingSessions() {
        return recordingSessions;
    }

    public void setRecordingSessions(Set<RecordingSession> recordingSessions) {
        this.recordingSessions = recordingSessions;
    }

    public Set<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(Set<Contributor> contributors) {
        this.contributors = contributors;
    }

    public Set<ReleaseSearchRelationEntity> getSearchRelations() {
        return searchRelations;
    }

    public void setSearchRelations(Set<ReleaseSearchRelationEntity> searchRelations) {
        this.searchRelations = searchRelations;
    }

    public String getSortAs() {
        return sortAs;
    }

    public void setSortAs(String sortAs) {
        this.sortAs = sortAs;
    }

    public void setSortAsAutomatically() {
        setSortAs(SortAsHelper.getSortAsForValue(Release.class.getSimpleName(), getName()));
    }

    public Image getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(Image defaultImage) {
        this.defaultImage = defaultImage;
    }

    public void addTrack(TrackEntity track) {
        if(Hibernate.isInitialized(tracks)) {
            this.tracks.add(track);
        }
        track.setRelease(this);
    }
    public void removeTrack(TrackEntity track) {
        if(Hibernate.isInitialized(tracks)) {
            this.tracks.remove(track);
        }
        track.setRelease(null);
    }

    public void addMedium(MediumEntity medium) {
        if(Hibernate.isInitialized(mediums)) {
            this.mediums.add(medium);
        }
        medium.setRelease(this);
    }
    public void removeMedium(MediumEntity medium) {
        if(Hibernate.isInitialized(mediums)) {
            this.mediums.remove(medium);
        }
        medium.setRelease(null);
    }

    public void addContributor(ContributorEntity contributor) {
        if(Hibernate.isInitialized(contributors)) {
            this.contributors.add(contributor);
        }
        contributor.setRelease(this);
    }
    public void removeContributor(ContributorEntity contributor) {
        if(Hibernate.isInitialized(contributors)) {
            this.contributors.remove(contributor);
        }
        contributor.setRelease(null);
    }
}
