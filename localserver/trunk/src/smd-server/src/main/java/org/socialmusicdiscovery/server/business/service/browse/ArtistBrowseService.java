package org.socialmusicdiscovery.server.business.service.browse;

import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.ArtistEntity;

import java.util.Collection;

public class ArtistBrowseService extends AbstractBrowseService implements BrowseService<Artist> {

    public Result<Artist> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean returnChildCounters) {
        return findChildren(ArtistEntity.class, "Artist","artist", "e.name", criteriaList, sortCriteriaList, firstItem, maxItems, returnChildCounters);
    }
}
