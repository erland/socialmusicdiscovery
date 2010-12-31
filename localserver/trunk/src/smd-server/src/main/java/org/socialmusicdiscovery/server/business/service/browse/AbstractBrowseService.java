package org.socialmusicdiscovery.server.business.service.browse;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;

public abstract class AbstractBrowseService<T extends SMDIdentity> {
    private String provides;

    @Inject
    EntityManager entityManager;

    public AbstractBrowseService(String provides) {
        InjectHelper.injectMembers(this);
        this.provides = provides;
    }

    protected String buildResultJoinString(String entityAlias, Collection<String> criteriaList) {
        StringBuffer joinString = new StringBuffer();
        int i = 0;
        for (String criteria : criteriaList) {
            i++;
            joinString.append(" JOIN ").append(entityAlias).append(".searchRelations as rel").append(i);
        }
        return joinString.toString();
    }

    protected String buildNativeResultJoinString(String entityAlias, String relationTable, Collection<String> criteriaList) {
        StringBuffer joinString = new StringBuffer();
        int i = 0;
        for (String criteria : criteriaList) {
            i++;
            joinString.append(" JOIN ").append(relationTable).append(" as rel").append(i).append(" ON rel").append(i).append(".id=").append(entityAlias).append(".id");
        }
        return joinString.toString();
    }

    protected String buildResultWhereString(Collection<String> criteriaList) {
        StringBuffer whereString = new StringBuffer();
        int i = 0;
        for (String criteria : criteriaList) {
            i++;
            if (whereString.length() > 0) {
                whereString.append(" AND");
            }
            if (criteria.contains(".") && !criteria.contains(":")) {
                if (criteria.startsWith(provides)) {
                    whereString.append(" rel").append(i).append(".type=:relSubType").append(i);
                } else {
                    whereString.append(" rel").append(i).append(".referenceType=:relType").append(i).append(" and rel").append(i).append(".type=:relSubType");
                }
            } else {
                whereString.append(" rel").append(i).append(".reference=:rel").append(i);
                if (criteria.contains(":")) {
                    if (criteria.contains(".")) {
                        whereString.append(" and ").append(" rel").append(i).append(".referenceType=:relType").append(i);
                        whereString.append(" and ").append(" rel").append(i).append(".type=:relSubType").append(i);
                    } else {
                        whereString.append(" and ").append(" rel").append(i).append(".referenceType=:relType").append(i);
                    }
                }
            }
        }
        return whereString.toString();
    }

    protected void setQueryParameters(Query query, Collection<String> criteriaList) {
        int j = 0;
        for (String criteria : criteriaList) {
            j++;
            if (criteria.contains(":")) {
                query.setParameter("rel" + j, criteria.substring(criteria.indexOf(":") + 1));
                if (criteria.contains(".")) {
                    query.setParameter("relType" + j, criteria.substring(0, criteria.indexOf(".")));
                    query.setParameter("relSubType" + j, criteria.substring(criteria.indexOf(".") + 1, criteria.indexOf(":")));
                } else {
                    query.setParameter("relType" + j, criteria.substring(0, criteria.indexOf(":")));
                }
            } else if (criteria.contains(".") && !criteria.contains(":")) {
                if (criteria.startsWith(provides)) {
                    query.setParameter("relSubType" + j, criteria.substring(provides.length() + 1));
                } else {
                    query.setParameter("relType" + j, criteria.substring(0, criteria.indexOf(".")));
                    query.setParameter("relSubType" + j, criteria.substring(criteria.indexOf(".") + 1));
                }
            } else {
                query.setParameter("rel" + j, criteria);
            }
        }
    }

    protected void setExclusionQueryParameters(Query query, Collection<String> criteriaList) {
        int j = 0;
        for (String criteria : criteriaList) {
            j++;
            if (criteria.contains(":")) {
                if (criteria.contains(".")) {
                    query.setParameter("exclRel" + j, criteria.substring(criteria.indexOf(":") + 1));
                    query.setParameter("exclRelSubType" + j, criteria.substring(criteria.indexOf(".") + 1, criteria.indexOf(":")));
                } else {
                    query.setParameter("exclRel" + j, criteria.substring(criteria.indexOf(":") + 1));
                }
                query.setParameter("exclRelType" + j, criteria.substring(0, criteria.indexOf(":")));
            } else if (criteria.contains(".") && !criteria.contains(":")) {
                // Don't use type criterias
            } else {
                query.setParameter("exclRel" + j, criteria);
            }
        }
    }

    protected String buildCountExclusionString(String entityAlias, Collection<String> criteriaList) {
        StringBuffer exclusions = new StringBuffer();
        int j = 0;
        for (String criteria : criteriaList) {
            j++;
            if (criteria.contains(":")) {
                if (criteria.contains(".")) {
                    exclusions.append(" AND NOT (").append(entityAlias).append(".reference=:exclRel").append(j).append(" AND ").append(entityAlias).append(".referenceType=:exclRelType").append(j).append(" AND ").append(entityAlias).append(".type=:exclRelSubType").append(j).append(")");
                } else {
                    exclusions.append(" AND NOT (").append(entityAlias).append(".reference=:exclRel").append(j).append(" AND ").append(entityAlias).append(".referenceType=:exclRelType").append(j).append(")");
                }
            } else if (criteria.contains(".") && !criteria.contains(":")) {
                // Don't exclude type criterias
            } else {
                exclusions.append(" AND ").append(entityAlias).append(".reference!=:exclRel").append(j);
            }
        }
        return exclusions.toString();
    }

    public Result<T> findChildren(Class entity, String tableName, String orderBy, Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean returnChildCounters) {
        String joinString = buildResultJoinString("r", criteriaList);
        String whereString = buildResultWhereString(criteriaList);

        Long count = null;
        if (maxItems != null) {
            Query countQuery = null;
            if (criteriaList.size() > 0) {
                String nativeJoinString = buildNativeResultJoinString("r", "recordings_search_relations", criteriaList);
                countQuery = entityManager.createNativeQuery("SELECT count(distinct e.id) from recordings as r JOIN recordings_search_relations as searchRelations ON searchRelations.id=r.id JOIN " + tableName + " as e ON e.id=searchRelations.reference " + nativeJoinString + " WHERE " + whereString + buildCountExclusionString("searchRelations", criteriaList));
                setExclusionQueryParameters(countQuery, criteriaList);
                setQueryParameters(countQuery, criteriaList);
                List<BigInteger> countList = countQuery.getResultList();
                count = countList.iterator().next().longValue();
            } else {
                countQuery = entityManager.createQuery("SELECT count(distinct e.id) from " + entity.getSimpleName() + " as e");
                List<Long> countList = countQuery.getResultList();
                count = countList.iterator().next();
            }
        }

        Result<T> result = new Result<T>();
        result.setCount(count);
        if (maxItems == null || count > 0L) {
            Query query = null;
            if (criteriaList.size() > 0) {
                String nativeJoinString = buildNativeResultJoinString("r", "recordings_search_relations", criteriaList);
                query = entityManager.createNativeQuery("SELECT distinct e.* from recordings as r JOIN recordings_search_relations as searchRelations ON searchRelations.id=r.id JOIN " + tableName + " as e ON e.id=searchRelations.reference " + nativeJoinString + " WHERE " + whereString + buildCountExclusionString("searchRelations", criteriaList) + (orderBy != null ? " order by " + orderBy : ""), entity);
                setExclusionQueryParameters(query, criteriaList);
                setQueryParameters(query, criteriaList);
            } else {
                query = entityManager.createQuery("SELECT distinct e from " + entity.getSimpleName() + " as e" + (orderBy != null ? " order by " + orderBy : ""));
            }
            if (firstItem != null) {
                query.setFirstResult(firstItem);
            }
            if (maxItems != null) {
                query.setMaxResults(maxItems);
            }
            List<T> items = query.getResultList();
            Collection<ResultItem<T>> resultItems = new ArrayList<ResultItem<T>>(items.size());
            result.setItems(resultItems);
            if (maxItems == null) {
                result.setCount((long) items.size());
            }

            for (T item : items) {
                if (returnChildCounters != null && returnChildCounters) {
                    Query countQuery = null;
                    if (criteriaList.size() > 0) {
                        countQuery = entityManager.createQuery("SELECT countRelations.referenceType,countRelations.type,count(distinct countRelations.reference) from RecordingEntity as r JOIN r.searchRelations as searchRelations " + joinString + " JOIN r.searchRelations as countRelations WHERE searchRelations.reference=:item AND " + whereString + " AND countRelations.reference!=:item " + buildCountExclusionString("countRelations", criteriaList) + " GROUP BY countRelations.referenceType,countRelations.type");
                        setExclusionQueryParameters(countQuery, criteriaList);
                    } else {
                        countQuery = entityManager.createQuery("SELECT countRelations.referenceType,countRelations.type,count(distinct countRelations.reference) from RecordingEntity as r JOIN r.searchRelations as searchRelations JOIN r.searchRelations as countRelations WHERE searchRelations.reference=:item AND countRelations.reference!=:item GROUP BY countRelations.referenceType,countRelations.type");
                    }
                    setQueryParameters(countQuery, criteriaList);
                    countQuery.setParameter("item", item.getId());
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
                    ResultItem<T> resultItem = new ResultItem<T>(item, childCounters);
                    resultItems.add(resultItem);
                } else {
                    ResultItem<T> resultItem = new ResultItem<T>(item);
                    resultItems.add(resultItem);
                }
            }
        }
        return result;
    }
}
