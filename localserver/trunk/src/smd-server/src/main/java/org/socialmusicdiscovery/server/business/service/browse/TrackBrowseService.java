package org.socialmusicdiscovery.server.business.service.browse;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.server.business.model.core.TrackEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class TrackBrowseService extends AbstractBrowseService<Track> implements BrowseService<Track> {
    public TrackBrowseService() {
        super(Track.class.getSimpleName());
    }

    public Result<Track> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean returnChildCounters) {
        // Tracks is a special case, we always get unlimited numbers since the sorting is done in Java code
        Result<Track> results = super.findChildren(TrackEntity.class, "tracks", null, criteriaList, sortCriteriaList, null, null, returnChildCounters);
        LinkedList<ResultItem<Track>> items = new LinkedList<ResultItem<Track>>(results.getItems());
        Collections.sort(items, new Comparator<ResultItem<Track>>() {
            @Override
            public int compare(ResultItem<Track> item1, ResultItem<Track> item2) {
                CompareToBuilder comparator = new CompareToBuilder();
                comparator.append(item1.getItem().getMedium() != null ? item1.getItem().getMedium().getNumber() : null, item2.getItem().getMedium() != null ? item2.getItem().getMedium().getNumber() : null);
                comparator.append(item1.getItem().getMedium() != null ? item1.getItem().getMedium().getName() : null, item2.getItem().getMedium() != null ? item2.getItem().getMedium().getName() : null);
                comparator.append(item1.getItem().getNumber(), item2.getItem().getNumber());
                return comparator.toComparison();
            }
        });
        if (firstItem != null) {
            while (firstItem > 0) {
                items.remove(0);
                firstItem--;
            }
        }
        if (maxItems != null) {
            int size = items.size();
            for (int i = maxItems; i < size; i++) {
                items.remove(maxItems.intValue());
            }
        }
        results.setItems(items);
        return results;
    }
}
