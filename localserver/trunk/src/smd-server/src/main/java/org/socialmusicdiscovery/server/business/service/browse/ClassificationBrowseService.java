package org.socialmusicdiscovery.server.business.service.browse;

import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;

import java.util.Collection;

public class ClassificationBrowseService extends AbstractBrowseService<Classification> implements BrowseService<Classification> {
    public ClassificationBrowseService() {
        super(Classification.class.getSimpleName());
    }

    public Result<Classification> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean returnChildCounters) {
        return super.findChildren(ClassificationEntity.class, "classification", "e.name", criteriaList, sortCriteriaList, firstItem, maxItems, returnChildCounters);
    }
}
