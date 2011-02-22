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
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.ContributorEntity;
import org.socialmusicdiscovery.server.business.model.core.RecordingEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPARecordingRepository extends AbstractJPASMDIdentityRepository<RecordingEntity> implements RecordingRepository {
    ContributorRepository contributorRepository;
    WorkRepository workRepository;

    @Inject
    public JPARecordingRepository(EntityManager em, ContributorRepository contributorRepository, WorkRepository workRepository) {
        super(em);
        this.contributorRepository = contributorRepository;
        this.workRepository = workRepository;
    }

    public Collection<RecordingEntity> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<RecordingEntity> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" where lower(e.name)=:name");
        query.setParameter("name",name.toLowerCase());
        return query.getResultList();
    }

    public Collection<RecordingEntity> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" where lower(e.name) like :name");
        query.setParameter("name","%"+name.toLowerCase()+"%");
        return query.getResultList();
    }
    @Override
    public void create(RecordingEntity entity) {
        if (entity.getMixOf() != null) {
            if(!entityManager.contains(entity.getMixOf())) {
                entity.setMixOf(findById(entity.getMixOf().getId()));
            }
        }
        for (Contributor contributor : entity.getContributors()) {
            if(!entityManager.contains(contributor)) {
                contributorRepository.create((ContributorEntity) contributor);
            }
        }
        super.create(entity);
    }

    @Override
    public RecordingEntity merge(RecordingEntity entity) {
        if (entity.getMixOf() != null) {
            if(!entityManager.contains(entity.getMixOf())) {
                entity.setMixOf(findById(entity.getMixOf().getId()));
            }
        }
        for (Contributor contributor : entity.getContributors()) {
            if(!entityManager.contains(contributor)) {
                contributorRepository.merge((ContributorEntity) contributor);
            }
        }
        return super.merge(entity);
    }

    @Override
    public void remove(RecordingEntity entity) {
        entity.getLabelSearchRelations().clear();
        entity.getReleaseSearchRelations().clear();
        entity.getTrackSearchRelations().clear();
        entity.getWorkSearchRelations().clear();
        entity.getArtistSearchRelations().clear();
        entity.getClassificationSearchRelations().clear();
        entityManager.createQuery("DELETE from ReleaseSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();

        entityManager.createNativeQuery("DELETE from classification_references where reference_id=:id").setParameter("id",entity.getId()).executeUpdate();
        super.remove(entity);
    }
}
