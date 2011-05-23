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

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Represents a release of a specific box,album, aingle or {@link Recording}. The UK and US releases of a specific
 * album is represented as two individual {@link Release} objects where a relation to the release that represents
 * the master Release. They might for example have different album covers and/or content
 * (eg. different track order, bonus tracks etc).
 */
public interface Release extends SMDIdentity {
    final static String TYPE = Release.class.getSimpleName();

    Date getDate();

    void setDate(Date date);

    String getName();

    void setName(String name);

    Label getLabel();

    void setLabel(Label label);

    List<Medium> getMediums();

    void setMediums(List<Medium> mediums);

    List<Track> getTracks();

    void setTracks(List<Track> tracks);

    Set<RecordingSession> getRecordingSessions();

    void setRecordingSessions(Set<RecordingSession> recordingSessions);

    Set<Contributor> getContributors();

    void setContributors(Set<Contributor> contributors);
}
