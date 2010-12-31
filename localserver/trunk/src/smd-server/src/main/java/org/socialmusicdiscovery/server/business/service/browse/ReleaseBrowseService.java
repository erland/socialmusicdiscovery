package org.socialmusicdiscovery.server.business.service.browse;

import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.ReleaseEntity;

import java.util.Collection;

public class ReleaseBrowseService extends AbstractBrowseService<Release> implements BrowseService<Release> {
    public ReleaseBrowseService() {
        super(Release.class.getSimpleName());
    }

    public Result<Release> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean returnChildCounters) {
        return super.findChildren(ReleaseEntity.class, "releases", "e.name", criteriaList, sortCriteriaList, firstItem, maxItems, returnChildCounters);
    }
}
