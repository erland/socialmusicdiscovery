package org.socialmusicdiscovery.server.business.service.browse;

import org.socialmusicdiscovery.server.business.model.core.Work;
import org.socialmusicdiscovery.server.business.model.core.WorkEntity;

import java.util.Collection;

public class WorkBrowseService extends AbstractBrowseService<Work> implements BrowseService<Work> {
    public WorkBrowseService() {
        super(Work.class.getSimpleName());
    }

    public Result<Work> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean returnChildCounters) {
        return super.findChildren(WorkEntity.class, "works", "e.name", criteriaList, sortCriteriaList, firstItem, maxItems, returnChildCounters);
    }
}
