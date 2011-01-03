package org.socialmusicdiscovery.server.business.service.browse;

import java.util.HashMap;
import java.util.Map;

public class ResultItem<T> {
    private Map<String, Long> childItems;
    private String type;
    private String id;
    private T item;

    public ResultItem() {
    }

    public ResultItem(T item) {
        this.item = item;
    }

    public ResultItem(T item, String type, String id) {
        this.item = item;
        this.id = id;
        this.type = type;
    }

    public ResultItem(T item, Map<String, Long> childCounters) {
        this.item = item;
        this.id = null;
        this.type = item.getClass().getSimpleName();
        childItems = new HashMap<String, Long>(childCounters);
    }

    public ResultItem(T item, String type, String id, Map<String, Long> childCounters) {
        this.item = item;
        this.id = id;
        this.type = type;
        childItems = new HashMap<String, Long>(childCounters);
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
}
