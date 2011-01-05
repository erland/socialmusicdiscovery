/*
 * Copyright 2010, Social Music Discovery project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Social Music Discovery project nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.socialmusicdiscovery.server.api.query;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class Result {
    public static class Child {
        @Expose
        private String id;
        @Expose
        private Long count;

        public Child() {
        }

        ;

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

    public static class ResultItem {
        @Expose
        private Collection<Child> childItems;
        @Expose
        private String id;
        @Expose
        private String name;
        @Expose
        private String type;
        @Expose
        private Object item;

        public ResultItem() {
        }

        public ResultItem(Object item) {
            this.item = item;
        }

        public ResultItem(Object item, String type, String id, String name) {
            this.item = item;
            this.type = type;
            this.id = id;
            this.name = name;
        }

        public ResultItem(Object item, Map<String, Long> childCounters) {
            this.item = item;
            childItems = new ArrayList<Child>(childCounters.size());
            for (Map.Entry<String, Long> entry : childCounters.entrySet()) {
                childItems.add(new Child(entry.getKey(), entry.getValue()));
            }
        }

        public ResultItem(Object item, String type, String id, String name, Map<String, Long> childCounters) {
            this.item = item;
            this.type = type;
            this.id=id;
            this.name=name;
            childItems = new ArrayList<Child>(childCounters.size());
            for (Map.Entry<String, Long> entry : childCounters.entrySet()) {
                childItems.add(new Child(entry.getKey(), entry.getValue()));
            }
        }

        public Collection<Child> getChildItems() {
            return childItems;
        }

        public Object getItem() {
            return item;
        }
    }

    @Expose
    private Collection<ResultItem> items;
    @Expose
    private Long totalSize;
    @Expose
    private Long offset;
    @Expose
    private Long size;

    public Result() {
    }

    public Result(Collection<ResultItem> items, Long totalSize, Long offset, Long size) {
        this.items = items;
        this.totalSize = totalSize;
        this.offset = offset;
        this.size = size;
    }

    public Collection<ResultItem> getItems() {
        return items;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public Long getOffset() {
        return offset;
    }

    public Long getSize() {
        return size;
    }
}
