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
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * See {@link Track}
 */
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

    @OneToMany(targetEntity = PlayableElementEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "track_id")
    @Expose
    private Set<PlayableElement> playableElements = new HashSet<PlayableElement>();

    @ManyToOne(targetEntity = MediumEntity.class)
    @JoinColumn(name = "medium_id")
    @Expose
    private Medium medium;

    @ManyToOne(targetEntity = ReleaseEntity.class)
    @JoinColumn(name = "release_id")
    @Expose
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
