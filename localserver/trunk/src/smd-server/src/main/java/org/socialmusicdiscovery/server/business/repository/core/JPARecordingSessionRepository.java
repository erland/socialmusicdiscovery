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
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPARecordingSessionRepository extends AbstractJPASMDIdentityRepository<RecordingSessionEntity> implements RecordingSessionRepository {
    ContributorRepository contributorRepository;
    RecordingRepository recordingRepository;

    @Inject
    public JPARecordingSessionRepository(EntityManager em, ContributorRepository contributorRepository, RecordingRepository recordingRepository) {
        super(em);
        this.contributorRepository = contributorRepository;
        this.recordingRepository = recordingRepository;
    }

    public Collection<RecordingSessionEntity> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        StringBuffer queryString = new StringBuffer(200);
        String distinct = "distinct ";
        queryString.append("select ").append(distinct).append("e").append(" from ").append("ReleaseEntity as r JOIN r.recordingSessions as e");
        if(mandatoryRelations != null) {
            for (String relation : mandatoryRelations) {
                queryString.append(" JOIN FETCH ").append("e").append(".").append(relation);
            }
        }
        if(optionalRelations != null) {
            for (String relation : optionalRelations) {
                queryString.append(" LEFT JOIN FETCH ").append("e").append(".").append(relation);
            }
        }

        Query query = entityManager.createQuery(queryString.toString()+" WHERE r.id=:release");
        query.setParameter("release", releaseId);
        return query.getResultList();
    }

    @Override
    public void create(RecordingSessionEntity entity) {
        for (Recording recording : entity.getRecordings()) {
            RecordingEntity existingRecording = recordingRepository.findById(recording.getId());
            if(existingRecording==null) {
                if(((RecordingEntity)recording).getLastUpdated()==null) {
                    ((RecordingEntity)recording).setLastUpdated(entity.getLastUpdated());
                }
                if(((RecordingEntity)recording).getLastUpdatedBy()==null) {
                    ((RecordingEntity)recording).setLastUpdatedBy(entity.getLastUpdatedBy());
                }
                entity.getRecordings().remove(recording);
                entity.addRecording((RecordingEntity) recording);
                recordingRepository.create((RecordingEntity) recording);
            }else {
                entity.getRecordings().remove(existingRecording);
                entity.addRecording(existingRecording);
            }
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
    public RecordingSessionEntity merge(RecordingSessionEntity entity) {
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
        for (Recording recording : entity.getRecordings()) {
            RecordingEntity existingRecording = recordingRepository.findById(recording.getId());
            if(existingRecording==null) {
                if(((RecordingEntity)recording).getLastUpdated()==null) {
                    ((RecordingEntity)recording).setLastUpdated(entity.getLastUpdated());
                }
                if(((RecordingEntity)recording).getLastUpdatedBy()==null) {
                    ((RecordingEntity)recording).setLastUpdatedBy(entity.getLastUpdatedBy());
                }
                entity.getRecordings().remove(recording);
                entity.addRecording((RecordingEntity) recording);
                recordingRepository.create((RecordingEntity) recording);
            }else {
                entity.getRecordings().remove(existingRecording);
                entity.addRecording(existingRecording);
            }
        }
        return super.merge(entity);
    }

    @Override
    public void remove(RecordingSessionEntity entity) {
        for (Recording recording : entity.getRecordings()) {
            recordingRepository.remove((RecordingEntity)recording);
        }
        super.remove(entity);
    }

    public void refresh(RecordingSessionEntity entity) {
        for (Recording recording : entity.getRecordings()) {
            recordingRepository.refresh((RecordingEntity) recording);
        }
    }
}
