package org.socialmusicdiscovery.server.business.service.browse;

import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.core.Track;

import javax.persistence.Query;
import java.util.*;

public class TrackBrowseService extends AbstractBrowseService implements BrowseService<Track> {
    public TrackBrowseService() {
        super(Track.class.getSimpleName());
    }

    public Result<Track> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean returnChildCounters) {
        String joinString = null;
        String countJoinString = null;
        if (criteriaList.size() > 0) {
            countJoinString = buildResultJoinString("e", criteriaList);
            joinString = " JOIN FETCH e.recording as r JOIN FETCH r.work LEFT JOIN FETCH e.medium" + countJoinString;
        }
        String whereString = buildResultWhereString(criteriaList);

        Long count = null;
        if (maxItems != null) {
            Query countQuery = null;
            if (criteriaList.size() > 0) {
                countQuery = entityManager.createQuery("SELECT count(distinct e.id) from TrackEntity as e " + countJoinString + " WHERE " + whereString);
                setQueryParameters(countQuery, criteriaList);
            } else {
                countQuery = entityManager.createQuery("SELECT count(distinct e.id) from TrackEntity as e");
            }

            List<Long> countList = countQuery.getResultList();
            count = countList.iterator().next();
        }

        Result<Track> result = new Result<Track>();
        result.setCount(count);
        if (maxItems == null || count > 0L) {
            Query query = null;
            if (criteriaList.size() > 0) {
                query = entityManager.createQuery("SELECT distinct e from TrackEntity as e " + joinString + " WHERE " + whereString + " order by e.number");
                setQueryParameters(query, criteriaList);
            } else {
                query = entityManager.createQuery("SELECT e from TrackEntity as e JOIN FETCH e.recording as r JOIN FETCH r.work LEFT JOIN FETCH e.medium order by e.number");
            }
            if (firstItem != null) {
                query.setFirstResult(firstItem);
            }
            if (maxItems != null) {
                query.setMaxResults(maxItems);
            }
            List<Track> tracks = query.getResultList();

            Collection<ResultItem<Track>> resultItems = new ArrayList<ResultItem<Track>>(tracks.size());
            result.setItems(resultItems);
            if (maxItems == null) {
                result.setCount((long) tracks.size());
            }
            for (Track track : tracks) {
                if (returnChildCounters != null && returnChildCounters) {
                    Query countQuery = null;
                    if (criteriaList.size() > 0) {
                        String exclusions = buildCountExclusionString("e", criteriaList);
                        countQuery = entityManager.createQuery("SELECT e.referenceType,e.type,count(*) from TrackSearchRelationEntity as e WHERE e.id=:track " + exclusions + " GROUP BY e.referenceType,e.type");
                        setExclusionQueryParameters(countQuery, criteriaList);
                    } else {
                        countQuery = entityManager.createQuery("SELECT e.referenceType,e.type,count(*) from TrackSearchRelationEntity as e WHERE e.id=:track GROUP BY e.referenceType,e.type");
                    }
                    countQuery.setParameter("track", track.getId());
                    List<Object[]> counts = countQuery.getResultList();
                    Map<String, Long> childCounters = new HashMap<String, Long>();
                    for (Object[] objects : counts) {
                        String referenceType = (String) objects[0];
                        String type = "";
                        if (!objects[1].equals("")) {
                            type = "." + objects[1];
                        }
                        if (InjectHelper.existsWithName(BrowseService.class, referenceType)) {
                            childCounters.put(referenceType + type, ((Long) objects[2]));
                        }
                    }
                    ResultItem<Track> resultItem = new ResultItem<Track>(track, childCounters);
                    resultItems.add(resultItem);
                } else {
                    ResultItem<Track> resultItem = new ResultItem<Track>(track);
                    resultItems.add(resultItem);
                }
            }
        }
        return result;
    }
}
