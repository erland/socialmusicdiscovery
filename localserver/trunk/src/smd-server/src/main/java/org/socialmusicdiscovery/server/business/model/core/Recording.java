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
import java.util.Set;

/**
 * Represents a specific recording of a {@link Work}, could be either a studio recording or a live concert recording.
 * If the {@link Work} is a symphony by Beethoven the {@link Recording} would be a recorded performance of that symphony
 * with a specific conductor. A Remix of a recording, for example "radio edit" or "clean version", is a separate recording.
 * The recording can always be represented as a single music files, if multiple recordings are required to represent
 * a complete {@link Work}, it's recommended to use a {@link RecordingSession} to represent the {@link Work} and a
 * {@link Recording} for each part of the work.
 */
public interface Recording extends SMDIdentity {
    final static String TYPE = Recording.class.getSimpleName();

    String getName();

    void setName(String name);

    Date getDate();

    void setDate(Date date);

    Recording getMixOf();

    void setMixOf(Recording mixOf);

    Set<Contributor> getContributors();

    void setContributors(Set<Contributor> contributors);

    Set<Work> getWorks();

    void setWorks(Set<Work> works);
}
