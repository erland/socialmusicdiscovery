/*
 *  Copyright 2010-2011, Social Music Discovery project
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of Social Music Discovery project nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.socialmusicdiscovery.server.business.service.browse;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.api.ConfigurationContext;
import org.socialmusicdiscovery.server.business.logic.ImageProviderManager;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.core.Image;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

public abstract class AbstractBrowseService {
    @Inject
    EntityManager entityManager;

    @Inject
    protected BrowseServiceManager browseServiceManager;

    @Inject
    protected ImageProviderManager imageProviderManager;

    protected ConfigurationContext configurationContext;

    public AbstractBrowseService() {
        InjectHelper.injectMembers(this);
    }

    protected static interface SortKeyProvider {
        public String getSortKey(Object item);
    }
    /**
     * Set configuration context for this browse service
     *
     * @param configurationContext The configuration context to use
     */
    public void setConfiguration(ConfigurationContext configurationContext) {
        this.configurationContext = configurationContext;
    }

    /**
     * Get configuration context for this browse service
     *
     * @return The configuration context
     */
    protected ConfigurationContext getConfiguration() {
        return configurationContext;
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
            } else if (criteria.contains(".") && !criteria.substring(0, criteria.indexOf(".")).equals(objectType)) {
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
                if (relationType.equals(criteria.substring(0, criteria.indexOf(".")))) {
                    whereString.append(" ").append(relationName).append(".type=:relType");
                } else {
                    whereString.append(" ").append("rel").append(i).append(".type=:relType").append(i);
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
                if (relationType.equals(criteria.substring(0, criteria.indexOf(".")))) {
                    query.setParameter("relType", criteria.substring(criteria.indexOf(".") + 1));
                } else {
                    query.setParameter("relType" + j, criteria.substring(criteria.indexOf(".") + 1));
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
            } else if (!criteria.contains(".")) {
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
            } else if (!criteria.contains(".")) {
                throw new RuntimeException("Type of criteria not specified: " + criteria);
            }
        }
        return exclusions.toString();
    }

    protected Query createCountQuery(Class entity, String objectType, String relationType, Collection<String> criteriaList, String joinString, String whereString) {
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

    protected <T extends SMDIdentity> ResultItem<T> findById(Class<T> entity, String objectType, String id) {
        T instance = entityManager.find(entity, id);
        ResultItem item = new ResultItem<T>(instance, getPlayable(), false);
        item.setImage(getImage(instance));
        item.setId(objectType + ":" + instance.getId());
        item.setType(objectType);
        return item;
    }

    protected <T extends SMDIdentity, E extends T> Integer findChildrenCount(Class<T> entity, String objectType, String relationType, Collection<String> criteriaList) {
        String joinString = buildResultJoinString(objectType, "r", criteriaList);
        String whereString = buildResultWhereString(objectType, "searchRelations", criteriaList);

        return findChildrenCount(entity, joinString, whereString, objectType, relationType, criteriaList);
    }

    protected <T extends SMDIdentity, E extends T> Integer findChildrenCount(Class<T> entity, String joinString, String whereString, String objectType, String relationType, Collection<String> criteriaList) {
        Query countQuery = createCountQuery(entity, objectType, relationType, criteriaList, joinString, whereString);
        List<Long> countList = countQuery.getResultList();
        return countList.iterator().next().intValue();
    }

    protected <T extends SMDIdentity, E extends T> Result<T> findChildren(Class<T> entity, String objectType, String relationType, String orderBy, Collection<String> criteriaList, Collection<String> sortCriteriaList, SortKeyProvider sortKeyProvider, Integer firstItem, Integer maxItems, Boolean returnChildCounters) {
        String joinString = buildResultJoinString(objectType, "r", criteriaList);
        String whereString = buildResultWhereString(objectType, "searchRelations", criteriaList);

        Integer count = null;
        if (maxItems != null) {
            count = findChildrenCount(entity, joinString, whereString, objectType, relationType, criteriaList);
        }

        Result<T> result = new Result<T>();
        result.setCount(count);
        if (maxItems == null || count > 0) {
            Query query = createFindQuery(entity, objectType, relationType, orderBy, criteriaList, sortCriteriaList, joinString, whereString);
            if (firstItem != null) {
                query.setFirstResult(firstItem);
            }
            if (maxItems != null) {
                query.setMaxResults(maxItems);
            }
            List<T> items = query.getResultList();
            List<ResultItem<T>> resultItems = new ArrayList<ResultItem<T>>(items.size());
            result.setItems(resultItems);
            if (maxItems == null) {
                result.setCount(items.size());
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
                                if (!criteria.contains(":") && criteria.contains(".") && criteria.substring(0, criteria.indexOf(".")).equals(objectType)) {
                                    excludeByType = " AND countRelations.type=:type ";
                                    excludedType = criteria.substring(criteria.indexOf(".") + 1);
                                }
                            }
                            countQuery = entityManager.createQuery("SELECT countRelations.referenceType,countRelations.type,count(distinct countRelations.reference) from RecordingEntity as r JOIN r." + relationType + "SearchRelations as searchRelations " + joinString + " JOIN r." + relation + "SearchRelations as countRelations WHERE searchRelations.reference=:item AND " + whereString + " AND NOT (countRelations.reference=:item " + excludeByType + ") " + buildExclusionString("countRelations", criteriaList) + " GROUP BY countRelations.referenceType,countRelations.type");
                            setExclusionQueryParameters(countQuery, criteriaList);
                            if (excludedType != null) {
                                countQuery.setParameter("type", excludedType);
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
                            if (browseServiceManager.getBrowseService(referenceType) != null) {
                                childCounters.put(referenceType + type, ((Long) objects[2]));
                            }
                        }
                    }

                    ResultItem<T> resultItem = new ResultItem<T>(item, getPlayable(), childCounters);
                    if(sortKeyProvider !=null) {
                        resultItem.setSortKey(sortKeyProvider.getSortKey(item));
                    }
                    resultItem.setImage(getImage(item));
                    resultItems.add(resultItem);
                } else {
                    ResultItem<T> resultItem = new ResultItem<T>(item, getPlayable(), false);
                    if(sortKeyProvider !=null) {
                        resultItem.setSortKey(sortKeyProvider.getSortKey(item));
                    }
                    resultItem.setImage(getImage(item));
                    resultItems.add(resultItem);
                }
            }
        }
        return result;
    }

    protected <T extends SMDIdentity> ResultItem.ResultItemImage getImage(T item) {
        Image image = getPersistentImage(item);
        if(image!=null) {
            String url = imageProviderManager.getProvider(image.getProviderId()).getImageURL(image);
            return new ResultItem.ResultItemImage(image.getProviderId(), image.getProviderImageId(), url);
        }
        return null;
    }

    protected <T extends SMDIdentity> Image getPersistentImage(T item) {
        return null;
    }

    protected Boolean getPlayable() {
        return true;
    }

    protected Map<String, Long> findObjectTypes(Collection<String> criteriaList, Boolean returnCounters) {
        String joinString = buildResultJoinString("", "r", criteriaList);
        String whereString = buildResultWhereString("", "searchRelations", criteriaList);

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
                setQueryParameters("", countQuery, criteriaList);
                List<Object[]> counts = countQuery.getResultList();
                for (Object[] objects : counts) {
                    String referenceType = (String) objects[0];
                    String type = "";
                    if (!objects[1].equals("")) {
                        type = "." + objects[1];
                    }
                    if (browseServiceManager.getBrowseService(referenceType) != null) {
                        childCounters.put(referenceType + type, ((Long) objects[2]));
                    }
                }
            }
        } else {
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
                setQueryParameters("", countQuery, criteriaList);
                List<Object[]> counts = countQuery.getResultList();
                for (Object[] objects : counts) {
                    String referenceType = (String) objects[0];
                    String type = "";
                    if (!objects[1].equals("")) {
                        type = "." + objects[1];
                    }
                    if (browseServiceManager.getBrowseService(referenceType) != null) {
                        childCounters.put(referenceType + type, null);
                    }
                }
            }
        }
        return childCounters;
    }
}
