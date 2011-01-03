package org.socialmusicdiscovery.server.business.service.browse;

import org.socialmusicdiscovery.server.business.model.core.Label;
import org.socialmusicdiscovery.server.business.model.core.LabelEntity;

import java.util.Collection;

public class LabelBrowseService extends AbstractBrowseService implements BrowseService<Label> {
    public Result<Label> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean returnChildCounters) {
        return super.findChildren(LabelEntity.class, "Label", "label", "e.name", criteriaList, sortCriteriaList, firstItem, maxItems, returnChildCounters);
    }
}
