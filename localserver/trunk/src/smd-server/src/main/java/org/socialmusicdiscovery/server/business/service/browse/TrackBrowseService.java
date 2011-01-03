package org.socialmusicdiscovery.server.business.service.browse;

import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.server.business.model.core.TrackEntity;

import javax.persistence.Query;
import java.util.Collection;

public class TrackBrowseService extends AbstractBrowseService implements BrowseService<Track> {
    protected Query createFindQuery(Class entity, String objectType, String relationType, String orderBy, Collection<String> criteriaList, Collection<String> sortCriteriaList, String joinString, String whereString) {
        Query query;
        if (criteriaList.size() > 0) {
            query = entityManager.createQuery("SELECT distinct e from RecordingEntity as r JOIN r."+relationType+"SearchRelations as searchRelations JOIN searchRelations."+relationType+" as e " + joinString + " LEFT JOIN FETCH e.medium as m WHERE " + whereString + buildExclusionString("searchRelations", criteriaList) + (orderBy != null ? " order by " + orderBy : ""));
            setExclusionQueryParameters(query, criteriaList);
            setQueryParameters(objectType, query, criteriaList);
        } else {
            query = entityManager.createQuery("SELECT distinct e from " + entity.getSimpleName() + " as e JOIN FETCH e.recording as r JOIN FETCH r.work LEFT JOIN FETCH e.medium as m " + (orderBy != null ? " order by " + orderBy : ""));
        }
        return query;
    }

    public Result<Track> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean returnChildCounters) {
        return findChildren(TrackEntity.class, "Track", "track", "m.number,m.name,e.number", criteriaList, sortCriteriaList, firstItem, maxItems, returnChildCounters);
    }
}
