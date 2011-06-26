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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultItem<T> {
    private Map<String, Long> childItems;
    private String type;
    private String id;
    private String name;
    private String sortKey;
    private ResultItemImage image;
    private Boolean playable;
    private String playableElementsURL;
    private String commandURL;
    private Boolean leaf;
    private List<String> parameters;
    private T item;

    public static class ResultItemImage {
        private String providerId;
        private String providerImageId;
        private String url;
        public ResultItemImage() {}
        public ResultItemImage(String providerId, String providerImageId, String url) {
            this.providerId = providerId;
            this.providerImageId = providerImageId;
            this.url = url;
        }

        public String getProviderId() {
            return providerId;
        }

        public void setProviderId(String providerId) {
            this.providerId = providerId;
        }

        public String getProviderImageId() {
            return providerImageId;
        }

        public void setProviderImageId(String providerImageId) {
            this.providerImageId = providerImageId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
    public ResultItem() {
    }

    public ResultItem(T item, Boolean playable, Boolean leaf) {
        this.item = item;
        this.playable = playable;
        this.leaf = leaf;
    }

    public ResultItem(T item, String type, String id, String name, Boolean playable, Boolean leaf) {
        this.item = item;
        this.id = id;
        this.type = type;
        this.name = name;
        this.playable = playable;
        this.leaf = leaf;
    }

    public ResultItem(T item, Boolean playable, Map<String, Long> childCounters) {
        this.item = item;
        this.id = null;
        this.type = item.getClass().getSimpleName();
        this.playable = playable;
        this.leaf = childCounters.size()==0;
        childItems = new HashMap<String, Long>(childCounters);
    }

    public ResultItem(T item, String type, String id, String name, Boolean playable, Map<String,Long> childCounters) {
        this.item = item;
        this.id = id;
        this.type = type;
        this.name = name;
        this.playable = playable;
        this.leaf = childCounters.size()==0;
        childItems = new HashMap<String, Long>(childCounters);
    }

    public ResultItemImage getImage() {
        return image;
    }

    public void setImage(ResultItemImage image) {
        this.image = image;
    }

    public Map<String, Long> getChildItems() {
        return childItems;
    }

    public void setChildItems(Map<String, Long> childItems) {
        this.childItems = childItems;
    }

    public T getItem() {
        return item;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Boolean getPlayable() {
        return playable;
    }

    public void setPlayable(Boolean playable) {
        this.playable = playable;
    }

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public String getPlayableElementsURL() {
        return playableElementsURL;
    }

    public void setPlayableElementsURL(String playableElementsURL) {
        this.playableElementsURL = playableElementsURL;
    }

    public String getCommandURL() {
        return commandURL;
    }

    public void setCommandURL(String commandURL) {
        this.commandURL = commandURL;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }
}
