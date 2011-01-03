package org.socialmusicdiscovery.server.business.service.browse;

import java.util.Collection;
import java.util.Map;

public class ObjectTypeBrowseService extends AbstractBrowseService {
    public ObjectTypeBrowseService() {
    }

    /**
     * Find object types that matches the specified search criterias
     * @param criteriaList A list of search criteras which the objects has to match
     * @param returnCounters true if counters for number of objects of each type should be returned
     * @return A map with the matching object types where the value optionally contains object counters
     */
    public Map<String, Long> findObjectTypes(Collection<String> criteriaList, Boolean returnCounters) {
        return super.findObjectTypes(criteriaList, returnCounters);
    }

    /**
     * Find object types that matches the specified search criterias
     * @param criteriaList A list of search criteras which the objects has to match
     * @return A list with the matching object types
     */
    public Collection<String> findTypes(Collection<String> criteriaList) {
        return super.findObjectTypes(criteriaList, false).keySet();
    }
}
