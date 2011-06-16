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

import org.socialmusicdiscovery.server.business.model.core.TrackEntity;

import javax.persistence.Query;
import java.util.Collection;

public class TrackBrowseService extends AbstractBrowseService implements BrowseService<TrackEntity> {
    @Override
    public ResultItem<TrackEntity> findById(String id) {
        return findById(TrackEntity.class, "Track", id);
    }

    protected Query createFindQuery(Class entity, String objectType, String relationType, String orderBy, Collection<String> criteriaList, Collection<String> sortCriteriaList, String joinString, String whereString) {
        Query query;
        if (criteriaList.size() > 0) {
            query = entityManager.createQuery("SELECT distinct e from RecordingEntity as r JOIN r."+relationType+"SearchRelations as searchRelations JOIN searchRelations."+relationType+" as e " + joinString + " LEFT JOIN FETCH e.medium as m LEFT JOIN FETCH e.playableElements as p WHERE " + whereString + buildExclusionString("searchRelations", criteriaList) + (orderBy != null ? " order by " + orderBy : ""));
            setExclusionQueryParameters(query, criteriaList);
            setQueryParameters(objectType, query, criteriaList);
        } else {
            query = entityManager.createQuery("SELECT distinct e from " + entity.getSimpleName() + " as e JOIN FETCH e.recording as r LEFT JOIN FETCH e.medium as m LEFT JOIN FETCH e.playableElements as p " + (orderBy != null ? " order by " + orderBy : ""));
        }
        return query;
    }

    public Result<TrackEntity> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean returnChildCounters) {
        return findChildren(TrackEntity.class, "Track", "track", "m.sortAs,e.number", criteriaList, sortCriteriaList, firstItem, maxItems, returnChildCounters);
    }

    @Override
    public String getObjectType() {
        return "Track";
    }
}
