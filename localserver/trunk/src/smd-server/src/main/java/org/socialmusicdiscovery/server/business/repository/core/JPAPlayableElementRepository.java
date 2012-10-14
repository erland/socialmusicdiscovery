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

package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.PlayableElementEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPAPlayableElementRepository extends AbstractJPASMDIdentityRepository<PlayableElementEntity> implements PlayableElementRepository {
    @Inject
    public JPAPlayableElementRepository(EntityManager em) {super(em);}

    public Collection<PlayableElementEntity> findBySmdID(String smdID) {
        return findBySmdIDWithRelations(smdID, null, null);
    }
    public Collection<PlayableElementEntity> findBySmdIDWithRelations(String smdID, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations,optionalRelations)+" where smdID=:smdID");
        query.setParameter("smdID",smdID);
        return query.getResultList();
    }
    public Collection<PlayableElementEntity> findByURIWithRelations(String uri, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.uri)=:uri order by e.uri");
        query.setParameter("uri", uri.toLowerCase());
        return query.getResultList();
    }

    public Collection<PlayableElementEntity> findByPartialURIWithRelations(String uriContains, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.uri) like :uri order by e.uri");
        query.setParameter("uri", "%" + uriContains.toLowerCase() + "%");
        return query.getResultList();
    }

    public Collection<PlayableElementEntity> findAllInSameRelease(PlayableElementEntity playableElement) {
        Query query = entityManager.createQuery(
        		"SELECT pe FROM TrackEntity t " +
        		"JOIN t.playableElements pe " +
        		"JOIN t.release r " +
        		"WHERE r IN  (" +
        		"	SELECT r FROM ReleaseEntity r " +
        		"	JOIN r.tracks t " +
        		"	JOIN t.playableElements pe " +
        		"	WHERE pe.id = :playableElementId" +
        		")" 
        );
        query.setParameter("playableElementId",playableElement.getId());
        return query.getResultList();
    }
}
