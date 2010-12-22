package org.socialmusicdiscovery.server.business.service.browse;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public abstract class AbstractBrowseService {
    private String provides;

    @Inject
    EntityManager entityManager;

    public AbstractBrowseService(String provides) {
        InjectHelper.injectMembers(this);
        this.provides = provides;
    }

    protected String buildResultJoinString(String entityAlias, Collection<String> criteriaList) {
        StringBuffer joinString = new StringBuffer();
        int i=0;
        for (String criteria : criteriaList) {
            i++;
            joinString.append(" JOIN ").append(entityAlias).append(".searchRelations as rel").append(i);
        }
        return joinString.toString();
    }

    protected String buildResultWhereString(Collection<String> criteriaList) {
        StringBuffer whereString = new StringBuffer();
        int i=0;
        for (String criteria : criteriaList) {
            i++;
            if(whereString.length()>0) {
                whereString.append(" AND");
            }
            if(criteria.contains(".") && !criteria.contains(":")) {
                if(criteria.startsWith(provides)) {
                    whereString.append(" rel").append(i).append(".type=:relSubType").append(i);
                }else {
                    whereString.append(" rel").append(i).append(".referenceType=:relType").append(i).append(" and rel").append(i).append(".type=:relSubType");
                }
            }else {
                whereString.append(" rel").append(i).append(".reference=:rel").append(i);
                if(criteria.contains(":")) {
                    if(criteria.contains(".")) {
                        whereString.append(" and ").append(" rel").append(i).append(".referenceType=:relType").append(i);
                        whereString.append(" and ").append(" rel").append(i).append(".type=:relSubType").append(i);
                    }else {
                        whereString.append(" and ").append(" rel").append(i).append(".referenceType=:relType").append(i);
                    }
                }
            }
        }
        return whereString.toString();
    }
    protected void setQueryParameters(Query query, Collection<String> criteriaList) {
        int j=0;
        for (String criteria : criteriaList) {
            j++;
            if(criteria.contains(":")) {
                query.setParameter("rel"+j,criteria.substring(criteria.indexOf(":")+1));
                if(criteria.contains(".")) {
                    query.setParameter("relType"+j,criteria.substring(0,criteria.indexOf(".")));
                    query.setParameter("relSubType"+j,criteria.substring(criteria.indexOf(".")+1,criteria.indexOf(":")));
                }else {
                    query.setParameter("relType"+j,criteria.substring(0,criteria.indexOf(":")));
                }
            }else if(criteria.contains(".") && !criteria.contains(":")) {
                if(criteria.startsWith(provides)) {
                    query.setParameter("relSubType"+j,criteria.substring(provides.length()+1));
                }else {
                    query.setParameter("relType"+j,criteria.substring(0,criteria.indexOf(".")));
                    query.setParameter("relSubType"+j,criteria.substring(criteria.indexOf(".")+1));
                }
            }else {
                query.setParameter("rel"+j,criteria);
            }
        }
    }

    protected void setExclusionQueryParameters(Query query, Collection<String> criteriaList) {
        int j=0;
        for (String criteria : criteriaList) {
            j++;
            if(criteria.contains(":")) {
                if(criteria.contains(".")) {
                    query.setParameter("rel"+j,criteria.substring(criteria.indexOf(":")+1));
                    query.setParameter("relSubType"+j,criteria.substring(criteria.indexOf(".")+1,criteria.indexOf(":")));
                }else {
                    query.setParameter("rel"+j,criteria.substring(criteria.indexOf(":")+1));
                }
                query.setParameter("relType"+j,criteria.substring(0,criteria.indexOf(":")));
            }else if(criteria.contains(".") && !criteria.contains(":")) {
                // Don't use type criterias
            }else {
                query.setParameter("rel"+j,criteria);
            }
        }
    }

    protected String buildCountExclusionString(String entityAlias, Collection<String> criteriaList) {
        StringBuffer exclusions = new StringBuffer();
        int j=0;
        for (String criteria : criteriaList) {
            j++;
            if(criteria.contains(":")) {
                if(criteria.contains(".")) {
                    exclusions.append(" AND NOT (").append(entityAlias).append(".reference=:rel").append(j).append(" AND ").append(entityAlias).append(".referenceType=:relType").append(j).append(" AND ").append(entityAlias).append(".type=:relSubType").append(j).append(")");
                }else {
                    exclusions.append(" AND NOT (").append(entityAlias).append(".reference=:rel").append(j).append(" AND ").append(entityAlias).append(".referenceType=:relType").append(j).append(")");
                }
            }else if(criteria.contains(".") && !criteria.contains(":")) {
                // Don't exclude type criterias
            }else {
                exclusions.append(" AND ").append(entityAlias).append(".reference!=:rel").append(j);
            }
        }
        return exclusions.toString();
    }
}
