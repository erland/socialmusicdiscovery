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

package org.socialmusicdiscovery.server.business.repository.subjective;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReference;
import org.socialmusicdiscovery.server.business.model.subjective.RelationEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPAEntityRepository;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPARelationRepository extends AbstractJPASMDIdentityRepository<RelationEntity> implements RelationRepository {
    @Inject
    public JPARelationRepository(EntityManager em) {super(em);}

    public Collection<RelationEntity> findRelationsFrom(SMDIdentityReference reference) {
        Query query = entityManager.createQuery("from RelationEntity where from_id=:from");
        query.setParameter("from",reference.getId());
        Collection<RelationEntity> result = query.getResultList();
        return result;
    }

    public Collection<RelationEntity> findRelationsFrom(SMDIdentityReference reference, Class relatedTo) {
        Query query = entityManager.createQuery("select r from RelationEntity r,SMDIdentityReferenceEntity ref where r.fromId=:from and r.toId=ref.id and ref.type=:type");
        query.setParameter("from",reference.getId());
        query.setParameter("type",relatedTo.getName());
        Collection<RelationEntity> result = query.getResultList();
        return result;
    }

    public Collection<RelationEntity> findRelationsTo(SMDIdentityReference reference) {
        Query query = entityManager.createQuery("from RelationEntity where to_id=:to");
        query.setParameter("to",reference.getId());
        Collection<RelationEntity> result = query.getResultList();
        return result;
    }

    public Collection<RelationEntity> findRelationsTo(SMDIdentityReference reference, Class relatedFrom) {
        Query query = entityManager.createQuery("r from RelationEntity r,SMDIdentityReferenceEntity ref where r.toId=:to and r.fromId=ref.id and ref.type=:relationType");
        query.setParameter("to",reference.getId());
        query.setParameter("relationType",relatedFrom.getName());
        Collection<RelationEntity> result = query.getResultList();
        return result;
    }
}
