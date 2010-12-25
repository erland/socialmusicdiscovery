package org.socialmusicdiscovery.server.business.service.browse;

import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.classification.Classification;

import javax.persistence.Query;
import java.util.*;

public class ClassificationBrowseService extends AbstractBrowseService implements BrowseService<Classification> {
    public ClassificationBrowseService() {
        super(Classification.class.getSimpleName());
    }
    public Collection<ResultItem<Classification>> findChildren(Collection<String> criteriaList,Collection<String> sortCriteriaList,Integer firstItem, Integer maxItems) {
        String joinString = buildResultJoinString("e",criteriaList);
        String whereString = buildResultWhereString(criteriaList);

        Query query = null;
        if(criteriaList.size()>0) {
            query = entityManager.createQuery("SELECT distinct e from ClassificationEntity as e "+joinString+" WHERE "+whereString+" order by e.name");
            setQueryParameters(query,criteriaList);
        }else {
            query = entityManager.createQuery("SELECT e from ClassificationEntity as e order by e.name");
        }
        if(firstItem!=null) {
            query.setFirstResult(firstItem);
        }
        if(maxItems!=null) {
            query.setMaxResults(maxItems);
        }
        List<Classification> classifications = query.getResultList();

        Collection<ResultItem<Classification>> result = new ArrayList<ResultItem<Classification>>(classifications.size());
        for (Classification classification : classifications) {
            Query countQuery = null;
            if(criteriaList.size()>0) {
                String exclusions = buildCountExclusionString("e",criteriaList);
                countQuery = entityManager.createQuery("SELECT e.referenceType,e.type,count(*) from ClassificationSearchRelationEntity as e WHERE e.id=:classification "+exclusions+" GROUP BY e.referenceType,e.type");
                setExclusionQueryParameters(countQuery,criteriaList);
            }else {
                countQuery = entityManager.createQuery("SELECT e.referenceType,e.type,count(*) from ClassificationSearchRelationEntity as e WHERE e.id=:classification GROUP BY e.referenceType,e.type");
            }
            countQuery.setParameter("classification", classification.getId());
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
            ResultItem<Classification> resultItem = new ResultItem<Classification>(classification,childCounters);
            result.add(resultItem);
        }

        return result;
    }
}
