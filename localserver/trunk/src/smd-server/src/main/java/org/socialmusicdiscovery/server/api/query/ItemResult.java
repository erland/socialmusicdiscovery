package org.socialmusicdiscovery.server.api.query;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ItemResult extends Result {
    /**
     * Represents a single item in the browsing result
     */
    public static class Item {
        @Expose
        private Collection<Child> childItems;
        @Expose
        private String id;
        @Expose
        private String name;
        @Expose
        private String type;
        @Expose
        private ItemImage image;
        @Expose
        private Object item;
        @Expose
        private String playable;
        @Expose
        private String command;
        @Expose
        private Boolean leaf;

        public Item() {
        }

        public Item(Object item, ItemImage image) {
            this.item = item;
            this.image = image;
        }

        public Item(Object item, ItemImage image, Boolean playable, String playableElementsURL, Boolean leaf) {
            this.item = item;
            this.image = image;
            if (playable) {
                this.playable = playableElementsURL;
            }
            this.leaf = leaf;
        }

        public Item(Object item, String type, String id, String name, ItemImage image, Boolean playable, String playableElementsURL, String command, Boolean leaf) {
            this.item = item;
            this.image = image;
            this.type = type;
            this.id = id;
            this.name = name;
            if (playable) {
                this.playable = playableElementsURL;
            }
            this.command = command;
            this.leaf = leaf;
        }

        public Item(Object item, ItemImage image, Boolean playable, String playableElementsURL, Map<String, Long> childCounters) {
            this.item = item;
            this.image = image;
            if (playable) {
                this.playable = playableElementsURL;
            }
            this.leaf = childCounters.size() == 0;
            childItems = new ArrayList<Child>(childCounters.size());
            for (Map.Entry<String, Long> entry : childCounters.entrySet()) {
                childItems.add(new Child(entry.getKey(), entry.getValue()));
            }
        }

        public Item(Object item, String type, String id, String name, ItemImage image, Boolean playable, String playableElementsURL, String command, Map<String, Long> childCounters) {
            this.item = item;
            this.image = image;
            this.type = type;
            this.id = id;
            this.name = name;
            if (playable) {
                this.playable = playableElementsURL;
            }
            this.command = command;
            this.leaf = childCounters.size() == 0;
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

    /**
     * Represent and image for an {@link Item}
     */
    public static class ItemImage {
        @Expose
        private String providerId;
        @Expose
        private String providerImageId;
        @Expose
        private String url;

        public ItemImage() {
        }
        public ItemImage(String providerId, String providerImageId, String url) {
            this.providerId = providerId;
            this.providerImageId = providerImageId;
            this.url = url;
        }
    }
    @Expose
    private List<Item> items;

    public ItemResult(List<Item> items, Long totalSize, Long offset, Long size) {
        super(totalSize, offset, size);
        this.items = items;
    }

    public ItemResult(List<Item> items, String playableElementsBaseURL, Long totalSize, Long offset, Long size) {
        super(playableElementsBaseURL, totalSize, offset, size);
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }
}
