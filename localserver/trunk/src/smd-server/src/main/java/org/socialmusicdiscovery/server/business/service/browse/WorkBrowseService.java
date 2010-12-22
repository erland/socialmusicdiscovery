package org.socialmusicdiscovery.server.business.service.browse;

import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.core.Work;

import javax.persistence.Query;
import java.util.*;

public class WorkBrowseService extends AbstractBrowseService implements BrowseService<Work> {
    public WorkBrowseService() {
        super(Work.class.getSimpleName());
    }
    public Collection<ResultItem<Work>> findChildren(Collection<String> criteriaList,Collection<String> sortCriteriaList,Integer firstItem, Integer maxItems) {
        String joinString = buildResultJoinString("e",criteriaList);
        String whereString = buildResultWhereString(criteriaList);

        Query query = null;
        if(criteriaList.size()>0) {
            query = entityManager.createQuery("SELECT distinct e from WorkEntity as e "+joinString+" WHERE "+whereString+" order by e.name");
            setQueryParameters(query,criteriaList);
        }else {
            query = entityManager.createQuery("SELECT e from WorkEntity as e order by e.name");
        }
        if(firstItem!=null) {
            query.setFirstResult(firstItem);
        }
        if(maxItems!=null) {
            query.setMaxResults(maxItems);
        }
        List<Work> works = query.getResultList();

        Collection<ResultItem<Work>> result = new ArrayList<ResultItem<Work>>(works.size());
        for (Work work : works) {
            Query countQuery = null;
            if(criteriaList.size()>0) {
                String exclusions = buildCountExclusionString("e",criteriaList);
                countQuery = entityManager.createQuery("SELECT e.referenceType,e.type,count(*) from WorkSearchRelationEntity as e WHERE e.id=:work "+exclusions+" GROUP BY e.referenceType,e.type");
                setExclusionQueryParameters(countQuery,criteriaList);
            }else {
                countQuery = entityManager.createQuery("SELECT e.referenceType,e.type,count(*) from WorkSearchRelationEntity as e WHERE e.id=:work GROUP BY e.referenceType,e.type");
            }
            countQuery.setParameter("work", work.getId());
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
            ResultItem<Work> resultItem = new ResultItem<Work>(work,childCounters);
            result.add(resultItem);
        }

        return result;
    }
}
