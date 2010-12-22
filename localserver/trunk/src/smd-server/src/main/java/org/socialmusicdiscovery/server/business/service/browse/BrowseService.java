package org.socialmusicdiscovery.server.business.service.browse;

import java.util.Collection;

public interface BrowseService <T> {
    Collection<ResultItem<T>> findChildren(Collection<String> criteriaList,Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems);
}
