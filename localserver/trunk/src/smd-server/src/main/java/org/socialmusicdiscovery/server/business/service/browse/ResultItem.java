package org.socialmusicdiscovery.server.business.service.browse;

import java.util.HashMap;
import java.util.Map;

public class ResultItem<T> {
    private Map<String,Long> childItems;
    private T item;
    public ResultItem() {}
    public ResultItem(T item, Map<String,Long> childCounters) {
        this.item = item;
        childItems = new HashMap<String,Long>(childCounters);
    }
    public Map<String,Long> getChildItems() {
        return childItems;
    }

    public T getItem() {
        return item;
    }
}
