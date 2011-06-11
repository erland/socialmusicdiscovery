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

package org.socialmusicdiscovery.server.business.service.browse;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * All browsing objects which are playable but not represented in SMD database should inherit
 * from this class. Objects represented in SMD database doesn't have to inherit from this class, for these
 * you just represent the browse object as the normal entity class.
 */
public abstract class AbstractPlayableBrowseObject implements SMDIdentity {
    @Expose
    private String id;
    @Expose
    private String name;
    @Expose
    private List<PlayableStream> playableElements = new ArrayList<PlayableStream>();

    /**
     * Represents a playable stream or file which should be played when the user selects to play
     * this object
     */
    public static class PlayableStream {
        @Expose
        String uri;

        /**
         * Constructs an empty instance, the {@link #uri} attribute has to be filled before it can be used
         */
        public PlayableStream() {
        }

        /**
         * Constructs a new instance with the specified uri
         *
         * @param uri The uri which represents this stream/file
         */
        public PlayableStream(String uri) {
            this.uri = uri;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }

    /**
     * Constructs an empty instance
     */
    public AbstractPlayableBrowseObject() {
    }

    /**
     * Constructs an instance with the specified id and name, the {@link #playableElements} attribute has to be filled separately.
     *
     * @param id   The identity of this object
     * @param name The name of this object which typically should be displayed to the user
     */
    public AbstractPlayableBrowseObject(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructs a new instance with the specified id and name and also attach a playable stream/file to it
     *
     * @param id          The identity of this object
     * @param name        The name of this object which typically should be displayed to the user
     * @param playableURI The uri which should be played when the user selects to play this object
     */
    public AbstractPlayableBrowseObject(String id, String name, String playableURI) {
        this.id = id;
        this.name = name;
        this.playableElements.add(new PlayableStream(playableURI));
    }

    /**
     * Constructs a new instance with the specified id and name and also attach a collection of playable streams/files to it
     *
     * @param id           The identity of this object
     * @param name         The name of this object which typically should be displayed to the user
     * @param playableURIs A collection of uris which should be played when the user selects to play this object
     */
    public AbstractPlayableBrowseObject(String id, String name, Collection<String> playableURIs) {
        this.id = id;
        this.name = name;
        for (String playableURI : playableURIs) {
            this.playableElements.add(new PlayableStream(playableURI));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PlayableStream> getPlayableElements() {
        return playableElements;
    }

    public void setPlayableElements(List<PlayableStream> playableElements) {
        this.playableElements = playableElements;
    }
}
