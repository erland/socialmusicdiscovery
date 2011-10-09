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
import org.socialmusicdiscovery.server.business.model.core.WorkEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPAWorkRepository extends AbstractJPASMDIdentityRepository<WorkEntity> implements WorkRepository {
    ContributorRepository contributorRepository;
    RecordingRepository recordingRepository;

    @Inject
    public JPAWorkRepository(EntityManager em, ContributorRepository contributorRepository, RecordingRepository recordingRepository) {
        super(em);
        this.contributorRepository = contributorRepository;
        this.recordingRepository = recordingRepository;
    }

    public Collection<WorkEntity> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<WorkEntity> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.name)=:name order by e.name");
        query.setParameter("name", name.toLowerCase());
        return query.getResultList();
    }

    public Collection<WorkEntity> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.name) like :name order by e.name");
        query.setParameter("name", "%" + name.toLowerCase() + "%");
        return query.getResultList();
    }

    public Collection<WorkEntity> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(recordingQueryStringFor("e", "work", "release", "relation", "searchRelation", mandatoryRelations, optionalRelations, true) + " WHERE searchRelation.reference=:release order by e.name");
        query.setParameter("release", releaseId);
        return query.getResultList();
    }

    public Collection<WorkEntity> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(recordingQueryStringFor("e", "work", "artist", "relation", "searchRelation", mandatoryRelations, optionalRelations, true) + " WHERE searchRelation.reference=:artist order by e.name");
        query.setParameter("artist", artistId);
        return query.getResultList();
    }

    @Override
    public Collection<WorkEntity> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations,optionalRelations) + " JOIN e.parent as parent where parent.id=:work order by e.name");
        query.setParameter("work", workId);
        return query.getResultList();
    }

    @Override
    public void create(WorkEntity entity) {
        if (entity.getParent() != null && !entityManager.contains(entity.getParent())) {
            entity.setParent(findById(entity.getParent().getId()));
        }
        if(entity.getSortAs()==null) {
            entity.setSortAsAutomatically();
        }
        super.create(entity);
         for (Contributor contributor : entity.getContributors()) {
            if(!entityManager.contains(contributor)) {
                if(((ContributorEntity)contributor).getLastUpdated()==null) {
                    ((ContributorEntity)contributor).setLastUpdated(entity.getLastUpdated());
                }
                if(((ContributorEntity)contributor).getLastUpdatedBy()==null) {
                    ((ContributorEntity)contributor).setLastUpdatedBy(entity.getLastUpdatedBy());
                }
                entity.addContributor((ContributorEntity) contributor);
                contributorRepository.create((ContributorEntity) contributor);
            }
        }
    }

    @Override
    public WorkEntity merge(WorkEntity entity) {
        if (entity.getParent() != null && !entityManager.contains(entity.getParent())) {
            entity.setParent(findById(entity.getParent().getId()));
        }
        for (Contributor contributor : entity.getContributors()) {
            if(!entityManager.contains(contributor)) {
                if(((ContributorEntity)contributor).getLastUpdated()==null) {
                    ((ContributorEntity)contributor).setLastUpdated(entity.getLastUpdated());
                }
                if(((ContributorEntity)contributor).getLastUpdatedBy()==null) {
                    ((ContributorEntity)contributor).setLastUpdatedBy(entity.getLastUpdatedBy());
                }
                entity.addContributor((ContributorEntity) contributor);
                contributorRepository.merge((ContributorEntity) contributor);
            }
        }
        if(entity.getSortAs()==null) {
            entity.setSortAsAutomatically();
        }
        return super.merge(entity);
    }


    @Override
    public void remove(WorkEntity entity) {
        Collection<RecordingEntity> recordings = recordingRepository.findBySearchRelation(entity);
        entityManager.createQuery("DELETE from RecordingWorkSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from ReleaseSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();

        entityManager.createNativeQuery("DELETE from classification_references where reference_to_id=:id").setParameter("id",entity.getId()).executeUpdate();
        super.remove(entity);
        for (RecordingEntity recording : recordings) {
            recordingRepository.refresh(recording);
        }
    }

    public void refresh(WorkEntity entity) {
        Collection<RecordingEntity> recordings = recordingRepository.findByWorkWithRelations(entity.getId(),null,null);
        for (RecordingEntity recording : recordings) {
            recordingRepository.refresh(recording);
        }
    }
}
