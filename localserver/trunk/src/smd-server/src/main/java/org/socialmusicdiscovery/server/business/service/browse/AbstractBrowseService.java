package org.socialmusicdiscovery.server.business.service.browse;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

public abstract class AbstractBrowseService {
    @Inject
    EntityManager entityManager;

    public AbstractBrowseService() {
        InjectHelper.injectMembers(this);
    }

    protected String buildResultJoinString(String objectType, String entityAlias, Collection<String> criteriaList) {
        StringBuffer joinString = new StringBuffer();
        int i = 0;
        for (String criteria : criteriaList) {
            i++;
            String referenceType;
            if (criteria.contains(".")) {
                referenceType = criteria.substring(0, criteria.indexOf(".")).toLowerCase();
            } else if (criteria.contains(":")) {
                referenceType = criteria.substring(0, criteria.indexOf(":")).toLowerCase();
            } else {
                throw new RuntimeException("Type of criteria not specified: " + criteria);
            }
            if (criteria.contains(":")) {
                joinString.append(" JOIN ").append(entityAlias).append(".").append(referenceType).append("SearchRelations as rel").append(i);
            }else if(criteria.contains(".") && !criteria.substring(0,criteria.indexOf(".")).equals(objectType)) {
                joinString.append(" JOIN ").append(entityAlias).append(".").append(referenceType).append("SearchRelations as rel").append(i);
            }
        }
        return joinString.toString();
    }

    protected String buildResultWhereString(String relationType, String relationName, Collection<String> criteriaList) {
        StringBuffer whereString = new StringBuffer();
        int i = 0;
        for (String criteria : criteriaList) {
            i++;
            if (whereString.length() > 0) {
                whereString.append(" AND");
            }
            if (criteria.contains(":")) {
                whereString.append(" rel").append(i).append(".reference=:rel").append(i);
                if (criteria.contains(".")) {
                    whereString.append(" and ").append(" rel").append(i).append(".type=:relType").append(i);
                }
            } else if (criteria.contains(".")) {
                if(relationType.equals(criteria.substring(0,criteria.indexOf(".")))) {
                    whereString.append(" ").append(relationName).append(".type=:relType");
                }else {
                    whereString.append(" ").append( "rel").append(i).append(".type=:relType").append(i);
                }
            } else {
                throw new RuntimeException("Type of criteria not specified: " + criteria);
            }
        }
        return whereString.toString();
    }

    protected void setQueryParameters(String relationType, Query query, Collection<String> criteriaList) {
        int j = 0;
        for (String criteria : criteriaList) {
            j++;
            if (criteria.contains(":")) {
                query.setParameter("rel" + j, criteria.substring(criteria.indexOf(":") + 1));
                if (criteria.contains(".")) {
                    query.setParameter("relType" + j, criteria.substring(criteria.indexOf(".") + 1, criteria.indexOf(":")));
                }
            } else if (criteria.contains(".")) {
                if(relationType.equals(criteria.substring(0,criteria.indexOf(".")))) {
                    query.setParameter("relType", criteria.substring(criteria.indexOf(".") + 1));
                }else {
                    query.setParameter("relType"+j, criteria.substring(criteria.indexOf(".") + 1));
                }
            } else {
                throw new RuntimeException("Type of criteria not specified: " + criteria);
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
                    query.setParameter("exclRelType" + j, criteria.substring(criteria.indexOf(".") + 1, criteria.indexOf(":")));
                } else {
                    query.setParameter("exclRel" + j, criteria.substring(criteria.indexOf(":") + 1));
                }
            } else if (criteria.contains(".")) {
                // Don't use type criterias
            } else {
                throw new RuntimeException("Type of criteria not specified: " + criteria);
            }
        }
    }

    protected String buildExclusionString(String entityAlias, Collection<String> criteriaList) {
        StringBuffer exclusions = new StringBuffer();
        int j = 0;
        for (String criteria : criteriaList) {
            j++;
            if (criteria.contains(":")) {
                if (criteria.contains(".")) {
                    exclusions.append(" AND NOT (").append(entityAlias).append(".reference=:exclRel").append(j).append(" AND ").append(entityAlias).append(".type=:exclRelType").append(j).append(")");
                } else {
                    exclusions.append(" AND NOT (").append(entityAlias).append(".reference=:exclRel").append(j).append(")");
                }
            } else if (criteria.contains(".")) {
                // Don't exclude type criterias
            } else {
                throw new RuntimeException("Type of criteria not specified: " + criteria);
            }
        }
        return exclusions.toString();
    }

    protected Query createCountQuery(Class entity, String objectType, String relationType, String orderBy, Collection<String> criteriaList, Collection<String> sortCriteriaList, String joinString, String whereString) {
        Query query;
        if (criteriaList.size() > 0) {
            query = entityManager.createQuery("SELECT count(distinct e.id) from RecordingEntity as r JOIN r." + relationType + "SearchRelations as searchRelations JOIN searchRelations." + relationType + " as e " + joinString + " WHERE " + whereString + buildExclusionString("searchRelations", criteriaList));
            setExclusionQueryParameters(query, criteriaList);
            setQueryParameters(objectType, query, criteriaList);
        } else {
            query = entityManager.createQuery("SELECT count(distinct e.id) from " + entity.getSimpleName() + " as e");
        }
        return query;
    }

    protected Query createFindQuery(Class entity, String objectType, String relationType, String orderBy, Collection<String> criteriaList, Collection<String> sortCriteriaList, String joinString, String whereString) {
        Query query;
        if (criteriaList.size() > 0) {
            query = entityManager.createQuery("SELECT distinct e from RecordingEntity as r JOIN r." + relationType + "SearchRelations as searchRelations JOIN searchRelations." + relationType + " as e " + joinString + " WHERE " + whereString + buildExclusionString("searchRelations", criteriaList) + (orderBy != null ? " order by " + orderBy : ""));
            setExclusionQueryParameters(query, criteriaList);
            setQueryParameters(objectType, query, criteriaList);
        } else {
            query = entityManager.createQuery("SELECT distinct e from " + entity.getSimpleName() + " as e" + (orderBy != null ? " order by " + orderBy : ""));
        }
        return query;
    }

    protected <T extends SMDIdentity> Result<T> findChildren(Class entity, String objectType, String relationType, String orderBy, Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean returnChildCounters) {
        String joinString = buildResultJoinString(objectType, "r", criteriaList);
        String whereString = buildResultWhereString(objectType, "searchRelations", criteriaList);

        Long count = null;
        if (maxItems != null) {
            Query countQuery = createCountQuery(entity, objectType, relationType, orderBy, criteriaList, sortCriteriaList, joinString, whereString);
            List<Long> countList = countQuery.getResultList();
            count = countList.iterator().next();
        }

        Result<T> result = new Result<T>();
        result.setCount(count);
        if (maxItems == null || count > 0L) {
            Query query = createFindQuery(entity, objectType, relationType, orderBy, criteriaList, sortCriteriaList, joinString, whereString);
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
                    List<String> relations = Arrays.asList("label", "release", "track", "work", "artist", "classification");
                    Map<String, Long> childCounters = new HashMap<String, Long>();
                    for (String relation : relations) {
                        Query countQuery = null;
                        if (criteriaList.size() > 0) {
                            String excludeByType = "";
                            String excludedType = null;
                            for (String criteria : criteriaList) {
                                if(!criteria.contains(":") && criteria.contains(".") && criteria.substring(0,criteria.indexOf(".")).equals(objectType)) {
                                    excludeByType = " AND countRelations.type=:type ";
                                    excludedType = criteria.substring(criteria.indexOf(".")+1);
                                }
                            }
                            countQuery = entityManager.createQuery("SELECT countRelations.referenceType,countRelations.type,count(distinct countRelations.reference) from RecordingEntity as r JOIN r." + relationType + "SearchRelations as searchRelations " + joinString + " JOIN r." + relation + "SearchRelations as countRelations WHERE searchRelations.reference=:item AND " + whereString + " AND NOT (countRelations.reference=:item " + excludeByType+") "+buildExclusionString("countRelations", criteriaList) + " GROUP BY countRelations.referenceType,countRelations.type");
                            setExclusionQueryParameters(countQuery, criteriaList);
                            if(excludedType!=null) {
                                countQuery.setParameter("type",excludedType);
                            }
                        } else {
                            countQuery = entityManager.createQuery("SELECT countRelations.referenceType,countRelations.type,count(distinct countRelations.reference) from RecordingEntity as r JOIN r." + relationType + "SearchRelations as searchRelations JOIN r." + relation + "SearchRelations as countRelations WHERE searchRelations.reference=:item AND countRelations.reference!=:item GROUP BY countRelations.referenceType,countRelations.type");
                        }
                        setQueryParameters(objectType, countQuery, criteriaList);
                        countQuery.setParameter("item", item.getId());
                        List<Object[]> counts = countQuery.getResultList();
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

    protected Map<String, Long> findObjectTypes(Collection<String> criteriaList, Boolean returnCounters) {
        String joinString = buildResultJoinString("","r", criteriaList);
        String whereString = buildResultWhereString("","searchRelations", criteriaList);

        Map<String, Long> childCounters = new HashMap<String, Long>();
        if (returnCounters != null && returnCounters) {
            List<String> relations = Arrays.asList("label", "release", "track", "work", "artist", "classification");
            for (String relation : relations) {
                Query countQuery = null;
                if (criteriaList.size() > 0) {
                    String queryString = "SELECT countRelations.referenceType,countRelations.type,count(distinct countRelations.reference) from RecordingEntity as r " + joinString + " JOIN r." + relation + "SearchRelations as countRelations WHERE " + whereString + buildExclusionString("countRelations", criteriaList) + " GROUP BY countRelations.referenceType,countRelations.type";
                    countQuery = entityManager.createQuery(queryString);
                    setExclusionQueryParameters(countQuery, criteriaList);
                } else {
                    countQuery = entityManager.createQuery("SELECT countRelations.referenceType,countRelations.type,count(distinct countRelations.reference) from RecordingEntity as r JOIN r." + relation + "SearchRelations as countRelations GROUP BY countRelations.referenceType,countRelations.type");
                }
                setQueryParameters("",countQuery, criteriaList);
                List<Object[]> counts = countQuery.getResultList();
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
            }
        }else {
            List<String> relations = Arrays.asList("label", "release", "track", "work", "artist", "classification");
            for (String relation : relations) {
                Query countQuery = null;
                if (criteriaList.size() > 0) {
                    String queryString = "SELECT distinct countRelations.referenceType,countRelations.type from RecordingEntity as r " + joinString + " JOIN r." + relation + "SearchRelations as countRelations WHERE " + whereString + buildExclusionString("countRelations", criteriaList);
                    countQuery = entityManager.createQuery(queryString);
                    setExclusionQueryParameters(countQuery, criteriaList);
                } else {
                    countQuery = entityManager.createQuery("SELECT distinct countRelations.referenceType,countRelations.type from RecordingEntity as r JOIN r." + relation + "SearchRelations as countRelations");
                }
                setQueryParameters("",countQuery, criteriaList);
                List<Object[]> counts = countQuery.getResultList();
                for (Object[] objects : counts) {
                    String referenceType = (String) objects[0];
                    String type = "";
                    if (!objects[1].equals("")) {
                        type = "." + objects[1];
                    }
                    if (InjectHelper.existsWithName(BrowseService.class, referenceType)) {
                        childCounters.put(referenceType + type, null);
                    }
                }
            }
        }
        return childCounters;
    }
}
