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
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;

import javax.persistence.*;

/**
 * See {@link Contributor}
 */
@javax.persistence.Entity
@Table(name = "contributors")
@SMDIdentityReferenceEntity.ReferenceType(type = Contributor.class)
public class ContributorEntity extends AbstractSMDIdentityEntity implements Contributor {
    @Expose
    @Transient
    private SMDIdentity owner;

    @ManyToOne(targetEntity = ReleaseEntity.class)
    @JoinColumn(name = "release_id")
    private Release release;

    @ManyToOne(targetEntity = WorkEntity.class)
    @JoinColumn(name = "work_id")
    private Work work;

    @ManyToOne(targetEntity = RecordingEntity.class)
    @JoinColumn(name = "recording_id")
    private Recording recording;

    @ManyToOne(targetEntity = RecordingSessionEntity.class)
    @JoinColumn(name = "session_id")
    private RecordingSession recordingSession;
/*
    @Column(name = "release_id")
    private String releaseId;

    @Column(name = "work_id")
    private String workId;

    @Column(name = "recording_id")
    private String recordingId;

    @Column(name = "session_id")
    private String recordingSessionId;
*/
    @Column(nullable = false)
    @Expose
    private String type;
    @ManyToOne(optional = false, targetEntity = ArtistEntity.class)
    @JoinColumn(name = "artist_id")
    @Expose
    private Artist artist;

    @PostLoad
    public void onLoad() {
        if(release!=null) {
            setOwner(release);
        }else if(work!=null) {
            setOwner(work);
        }else if(recording!=null) {
            setOwner(recording);
        }else if(recordingSession!=null){
            setOwner(recordingSession);
        }
    }

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

    public SMDIdentity getOwner() {
        return owner;
    }

    public void setOwner(SMDIdentity owner) {
        this.owner = owner;
        if(owner instanceof Release) {
            release = (Release) owner;
        }else if(owner instanceof Work) {
            work = (Work) owner;
        }else if(owner instanceof Recording) {
            recording = (Recording) owner;
        }else if(owner instanceof RecordingSession) {
            recordingSession = (RecordingSession) owner;
        }
    }

    public Release getRelease() {
        if(release==null && owner instanceof Release) {
            release = (Release) owner;
        }
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
        setOwner(release);
    }

    public Work getWork() {
        if(work==null && owner instanceof Work) {
            work = (Work) owner;
        }
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
        setOwner(work);
    }

    public Recording getRecording() {
        if(recording==null && owner instanceof Recording) {
            recording = (Recording) owner;
        }
        return recording;
    }

    public void setRecording(Recording recording) {
        this.recording = recording;
        setOwner(recording);
    }

    public RecordingSession getRecordingSession() {
        if(recordingSession==null && owner instanceof RecordingSession) {
            recordingSession = (RecordingSession) owner;
        }
        return recordingSession;
    }

    public void setRecordingSession(RecordingSession recordingSession) {
        this.recordingSession = recordingSession;
        setOwner(recordingSession);
    }
}
