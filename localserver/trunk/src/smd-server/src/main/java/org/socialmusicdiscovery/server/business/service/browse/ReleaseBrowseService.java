package org.socialmusicdiscovery.server.business.service.browse;

import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.core.Release;

import javax.persistence.Query;
import java.util.*;

public class ReleaseBrowseService extends AbstractBrowseService implements BrowseService<Release> {
    public ReleaseBrowseService() {
        super(Release.class.getSimpleName());
    }
    public Collection<ResultItem<Release>> findChildren(Collection<String> criteriaList,Collection<String> sortCriteriaList,Integer firstItem, Integer maxItems) {
        String joinString = buildResultJoinString("e",criteriaList);
        String whereString = buildResultWhereString(criteriaList);

        Query query = null;
        if(criteriaList.size()>0) {
            query = entityManager.createQuery("SELECT distinct e from ReleaseEntity as e "+joinString+" WHERE "+whereString+" order by e.name");
            setQueryParameters(query,criteriaList);
        }else {
            query = entityManager.createQuery("SELECT e from ReleaseEntity as e order by e.name");
        }
        if(firstItem!=null) {
            query.setFirstResult(firstItem);
        }
        if(maxItems!=null) {
            query.setMaxResults(maxItems);
        }
        List<Release> releases = query.getResultList();

        Collection<ResultItem<Release>> result = new ArrayList<ResultItem<Release>>(releases.size());
        for (Release release: releases) {
            Query countQuery = null;
            if(criteriaList.size()>0) {
                String exclusions = buildCountExclusionString("e",criteriaList);
                countQuery = entityManager.createQuery("SELECT e.referenceType,e.type,count(*) from ReleaseSearchRelationEntity as e WHERE e.id=:release "+exclusions+" GROUP BY e.referenceType,e.type");
                setExclusionQueryParameters(countQuery,criteriaList);
            }else {
                countQuery = entityManager.createQuery("SELECT e.referenceType,e.type,count(*) from ReleaseSearchRelationEntity as e WHERE e.id=:release GROUP BY e.referenceType,e.type");
            }
            countQuery.setParameter("release", release.getId());
            List<Object[]> counts = countQuery.getResultList();
            Map<String,Long> childCounters = new HashMap<String,Long>();
            for (Object[] objects : counts) {
                String referenceType = (String) objects[0];
                String type = "";
                if(!objects[1].equals("")) {
                    type = "."+objects[1];
                }
                if(InjectHelper.existsWithName(BrowseService.class,referenceType)) {
                    childCounters.put(referenceType+type,((Long)objects[2]));
                }
            }
            ResultItem<Release> resultItem = new ResultItem<Release>(release,childCounters);
            result.add(resultItem);
        }

        return result;
    }
}
