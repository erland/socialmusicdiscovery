package org.socialmusicdiscovery.server.business.service.browse;

import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.ArtistEntity;

import java.util.Collection;

public class ArtistBrowseService extends AbstractBrowseService<Artist> implements BrowseService<Artist> {
    public ArtistBrowseService() {
        super(Artist.class.getSimpleName());
    }

    public Result<Artist> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean returnChildCounters) {
        return super.findChildren(ArtistEntity.class, "artists", "e.name", criteriaList, sortCriteriaList, firstItem, maxItems, returnChildCounters);
    }
}
