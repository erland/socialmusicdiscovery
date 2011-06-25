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

/**
 * Represents a browsing result
 */
public abstract class Result {
    /**
     * Represents a counter for a specific type of objects which exists beneath an item in the browse result
     */
    public static class Child {
        @Expose
        private String id;
        @Expose
        private Long count;

        public Child() {
        }

        public Child(String id, Long count) {
            this.id = id;
            this.count = count;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }

    @Expose
    private String playableBaseURL;
    @Expose
    private Boolean alphabetic;
    @Expose
    private Integer totalSize;
    @Expose
    private Integer offset;
    @Expose
    private Integer size;

    public Result() {
    }

    public Result(Boolean alphabetic, Integer totalSize, Integer offset, Integer size) {
        this.totalSize = totalSize;
        this.offset = offset;
        this.size = size;
        this.alphabetic = alphabetic;
    }

    public Result(Boolean alphabetic, String playableBaseURL, Integer totalSize, Integer offset, Integer size) {
        this.totalSize = totalSize;
        this.offset = offset;
        this.size = size;
        this.playableBaseURL = playableBaseURL;
        this.alphabetic = alphabetic;
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getSize() {
        return size;
    }
}
