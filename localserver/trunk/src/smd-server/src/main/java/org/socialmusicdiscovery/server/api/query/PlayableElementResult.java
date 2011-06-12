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

package org.socialmusicdiscovery.server.api.query;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.core.PlayableElement;

import java.util.Collection;
import java.util.List;

/**
 * Represents a list of PlayableElement
 */
public class PlayableElementResult extends Result {

    /**
     * Represents a single playable element
     */
    public static class PlayableElementItem {
        @Expose
        private String uri;
        @Expose
        private String smdID;
        @Expose
        private String format;
        @Expose
        private Integer bitrate;

        public PlayableElementItem() {
        }

        public PlayableElementItem(PlayableElement item) {
            this.uri = item.getUri();
            this.smdID = item.getSmdID();
            this.format = item.getFormat();
            this.bitrate = item.getBitrate();
        }
    }

    @Expose
    private List<PlayableElementItem> items;

    public PlayableElementResult() {
    }

    public PlayableElementResult(List<PlayableElementItem> items, Long totalSize, Long offset, Long size) {
        super(totalSize, offset, size);
        this.items = items;
    }

    public Collection<PlayableElementItem> getItems() {
        return items;
    }
}
