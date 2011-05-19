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
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.search.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@javax.persistence.Entity
@Table(name = "recordings")
@SMDIdentityReferenceEntity.ReferenceType(type = Recording.class)
public class RecordingEntity extends AbstractSMDIdentityEntity implements Recording, ContributorOwner {
    @Expose
    private String name;
    @Expose
    private Date date;
    @ManyToOne(targetEntity = RecordingEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "mixof_id")
    @Expose
    private Recording mixOf;
    @OneToMany(targetEntity = ContributorEntity.class, mappedBy = "recording", cascade = {CascadeType.REMOVE}, orphanRemoval = true)
    private Set<Contributor> contributors = new HashSet<Contributor>();
    @ManyToMany(targetEntity = WorkEntity.class, fetch = FetchType.EAGER)
    @JoinTable(name = "recording_works",
            joinColumns = @JoinColumn(name = "recording_id"),
            inverseJoinColumns = @JoinColumn(name = "work_id"))
    @Expose
    private Set<Work> works = new HashSet<Work>();

    @ManyToOne(targetEntity = RecordingSessionEntity.class)
    @JoinColumn(name = "session_id")
    private RecordingSession recordingSession;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "id")
    private Set<RecordingLabelSearchRelationEntity> labelSearchRelations = new HashSet<RecordingLabelSearchRelationEntity>();
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "id")
    private Set<RecordingReleaseSearchRelationEntity> releaseSearchRelations = new HashSet<RecordingReleaseSearchRelationEntity>();
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "id")
    private Set<RecordingTrackSearchRelationEntity> trackSearchRelations = new HashSet<RecordingTrackSearchRelationEntity>();
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "id")
    private Set<RecordingWorkSearchRelationEntity> workSearchRelations = new HashSet<RecordingWorkSearchRelationEntity>();
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "id")
    private Set<RecordingArtistSearchRelationEntity> artistSearchRelations = new HashSet<RecordingArtistSearchRelationEntity>();
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "id")
    private Set<RecordingClassificationSearchRelationEntity> classificationSearchRelations = new HashSet<RecordingClassificationSearchRelationEntity>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Recording getMixOf() {
        return mixOf;
    }

    public void setMixOf(Recording mixOf) {
        this.mixOf = mixOf;
    }

    public Set<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(Set<Contributor> contributors) {
        this.contributors = contributors;
    }

    public Set<Work> getWorks() {
        return works;
    }

    public void setWorks(Set<Work> works) {
        this.works = works;
    }

    public Set<SearchRelationEntity> getSearchRelations() {
        Set<SearchRelationEntity> aggregatedSearchRelations = new HashSet<SearchRelationEntity>(
                getLabelSearchRelations().size()+
                getReleaseSearchRelations().size()+
                getTrackSearchRelations().size()+
                getWorkSearchRelations().size()+
                getArtistSearchRelations().size()+
                getClassificationSearchRelations().size());
        aggregatedSearchRelations.addAll(getLabelSearchRelations());
        aggregatedSearchRelations.addAll(getReleaseSearchRelations());
        aggregatedSearchRelations.addAll(getTrackSearchRelations());
        aggregatedSearchRelations.addAll(getWorkSearchRelations());
        aggregatedSearchRelations.addAll(getArtistSearchRelations());
        aggregatedSearchRelations.addAll(getClassificationSearchRelations());
        return aggregatedSearchRelations;
    }

    public Set<RecordingLabelSearchRelationEntity> getLabelSearchRelations() {
        return labelSearchRelations;
    }

    public void setLabelSearchRelations(Set<RecordingLabelSearchRelationEntity> labelSearchRelations) {
        this.labelSearchRelations = labelSearchRelations;
    }

    public Set<RecordingReleaseSearchRelationEntity> getReleaseSearchRelations() {
        return releaseSearchRelations;
    }

    public void setReleaseSearchRelations(Set<RecordingReleaseSearchRelationEntity> releaseSearchRelations) {
        this.releaseSearchRelations = releaseSearchRelations;
    }

    public Set<RecordingTrackSearchRelationEntity> getTrackSearchRelations() {
        return trackSearchRelations;
    }

    public void setTrackSearchRelations(Set<RecordingTrackSearchRelationEntity> trackSearchRelations) {
        this.trackSearchRelations = trackSearchRelations;
    }

    public Set<RecordingWorkSearchRelationEntity> getWorkSearchRelations() {
        return workSearchRelations;
    }

    public void setWorkSearchRelations(Set<RecordingWorkSearchRelationEntity> workSearchRelations) {
        this.workSearchRelations = workSearchRelations;
    }

    public Set<RecordingArtistSearchRelationEntity> getArtistSearchRelations() {
        return artistSearchRelations;
    }

    public void setArtistSearchRelations(Set<RecordingArtistSearchRelationEntity> artistSearchRelations) {
        this.artistSearchRelations = artistSearchRelations;
    }

    public Set<RecordingClassificationSearchRelationEntity> getClassificationSearchRelations() {
        return classificationSearchRelations;
    }

    public void setClassificationSearchRelations(Set<RecordingClassificationSearchRelationEntity> classificationSearchRelations) {
        this.classificationSearchRelations = classificationSearchRelations;
    }

    public RecordingSession getRecordingSession() {
        return recordingSession;
    }

    public void setRecordingSession(RecordingSession recordingSession) {
        this.recordingSession = recordingSession;
    }

    public void addContributor(ContributorEntity contributor) {
        if(Hibernate.isInitialized(contributors)) {
            this.contributors.add(contributor);
        }
        contributor.setRecording(this);
    }
    public void removeContributor(ContributorEntity contributor) {
        if(Hibernate.isInitialized(contributors)) {
            this.contributors.remove(contributor);
        }
        contributor.setRecording(null);
    }
}
