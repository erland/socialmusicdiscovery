package org.socialmusicdiscovery.server.business.service.browse;

import org.socialmusicdiscovery.server.business.model.core.Label;
import org.socialmusicdiscovery.server.business.model.core.LabelEntity;

import java.util.Collection;

public class LabelBrowseService extends AbstractBrowseService<Label> implements BrowseService<Label> {
    public LabelBrowseService() {
        super(Label.class.getSimpleName());
    }

    public Result<Label> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean returnChildCounters) {
        return super.findChildren(LabelEntity.class, "label", "e.name", criteriaList, sortCriteriaList, firstItem, maxItems, returnChildCounters);
    }
}
