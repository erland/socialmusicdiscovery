package org.socialmusicdiscovery.server.business.service.browse;

import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.core.Artist;

import javax.persistence.Query;
import java.util.*;

public class ArtistBrowseService extends AbstractBrowseService implements BrowseService<Artist> {
    public ArtistBrowseService() {
        super(Artist.class.getSimpleName());
    }
    public Collection<ResultItem<Artist>> findChildren(Collection<String> criteriaList,Collection<String> sortCriteriaList,Integer firstItem, Integer maxItems) {
        String joinString = buildResultJoinString("e",criteriaList);
        String whereString = buildResultWhereString(criteriaList);

        Query query = null;
        if(criteriaList.size()>0) {
            query = entityManager.createQuery("SELECT distinct e from ArtistEntity as e "+joinString+" WHERE "+whereString+" order by e.name");
            setQueryParameters(query,criteriaList);
        }else {
            query = entityManager.createQuery("SELECT e from ArtistEntity as e"+" order by e.name");
        }
        if(firstItem!=null) {
            query.setFirstResult(firstItem);
        }
        if(maxItems!=null) {
            query.setMaxResults(maxItems);
        }
        List<Artist> artists = query.getResultList();

        Collection<ResultItem<Artist>> result = new ArrayList<ResultItem<Artist>>(artists.size());
        for (Artist artist : artists) {
            Query countQuery = null;
            if(criteriaList.size()>0) {
                String exclusions = buildCountExclusionString("e",criteriaList);
                countQuery = entityManager.createQuery("SELECT e.referenceType,e.type,count(*) from ArtistSearchRelationEntity as e WHERE e.id=:artist "+exclusions+" GROUP BY e.referenceType,e.type");
                setExclusionQueryParameters(countQuery,criteriaList);
            }else {
                countQuery = entityManager.createQuery("SELECT e.referenceType,e.type,count(*) from ArtistSearchRelationEntity as e WHERE e.id=:artist GROUP BY e.referenceType,e.type");
            }
            countQuery.setParameter("artist", artist.getId());
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
            ResultItem<Artist> resultItem = new ResultItem<Artist>(artist,childCounters);
            result.add(resultItem);
        }

        return result;
    }
}
