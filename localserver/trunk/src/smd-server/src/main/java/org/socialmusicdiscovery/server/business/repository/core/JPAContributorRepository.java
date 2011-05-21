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
import org.hibernate.Hibernate;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPAContributorRepository extends AbstractJPASMDIdentityRepository<ContributorEntity> implements ContributorRepository {
    private ArtistRepository artistRepository;
    private ReleaseRepository releaseRepository;
    private WorkRepository workRepository;
    private RecordingRepository recordingRepository;
    private RecordingSessionRepository recordingSessionRepository;

    @Inject
    public JPAContributorRepository(EntityManager em, ArtistRepository artistRepository, ReleaseRepository releaseRepository, WorkRepository workRepository, RecordingRepository recordingRepository, RecordingSessionRepository recordingSessionRepository) {
        super(em);
        this.artistRepository = artistRepository;
        this.releaseRepository = releaseRepository;
        this.workRepository = workRepository;
        this.recordingRepository = recordingRepository;
        this.recordingSessionRepository = recordingSessionRepository;
    }

    public Collection<ContributorEntity> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.artist as a WHERE a.id=:artist");
        query.setParameter("artist", artistId);
        return query.getResultList();

    }

    public Collection<ContributorEntity> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        StringBuffer queryString = new StringBuffer(200);
        String distinct = "distinct ";
        queryString.append("select ").append(distinct).append("e").append(" from ").append("ReleaseEntity as r JOIN r.contributors as e");
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

    public Collection<ContributorEntity> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        StringBuffer queryString = new StringBuffer(200);
        String distinct = "distinct ";
        queryString.append("select ").append(distinct).append("e").append(" from ").append("WorkEntity as r JOIN r.contributors as e");
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

        Query query = entityManager.createQuery(queryString.toString()+" WHERE r.id=:work");
        query.setParameter("work", workId);
        return query.getResultList();

    }

    public Collection<ContributorEntity> findByRecordingWithRelations(String recordingId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        StringBuffer queryString = new StringBuffer(200);
        String distinct = "distinct ";
        queryString.append("select ").append(distinct).append("e").append(" from ").append("RecordingEntity as r JOIN r.contributors as e");
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

        Query query = entityManager.createQuery(queryString.toString()+" WHERE r.id=:recording");
        query.setParameter("recording", recordingId);
        return query.getResultList();

    }

    public Collection<ContributorEntity> findByRecordingSessionWithRelations(String recordingSessionId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        StringBuffer queryString = new StringBuffer(200);
        String distinct = "distinct ";
        queryString.append("select ").append(distinct).append("e").append(" from ").append("RecordingSessionEntity as r JOIN r.contributors as e");
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

        Query query = entityManager.createQuery(queryString.toString()+" WHERE r.id=:recordingSession");
        query.setParameter("recordingSession", recordingSessionId);
        return query.getResultList();

    }

    @Override
    public void create(ContributorEntity entity) {
        if (entity.getArtist() != null && !entityManager.contains(entity.getArtist())) {
            entity.setArtist(artistRepository.findById(entity.getArtist().getId()));
        }
        initializeOwner(entity);
        if (entity.getRelease() != null && !entityManager.contains(entity.getRelease())) {
            entity.setRelease(releaseRepository.findById(entity.getRelease().getId()));
        }else if (entity.getWork() != null && !entityManager.contains(entity.getWork())) {
            entity.setWork(workRepository.findById(entity.getWork().getId()));
        }else if (entity.getRecording() != null && !entityManager.contains(entity.getRecording())) {
            entity.setRecording(recordingRepository.findById(entity.getRecording().getId()));
        }else if (entity.getRecordingSession() != null && !entityManager.contains(entity.getRecordingSession())) {
            entity.setRecordingSession(recordingSessionRepository.findById(entity.getRecordingSession().getId()));
        }
        super.create(entity);
    }

    @Override
    public ContributorEntity merge(ContributorEntity entity) {
        if (entity.getArtist() != null && !entityManager.contains(entity.getArtist())) {
            entity.setArtist(artistRepository.findById(entity.getArtist().getId()));
        }
        initializeOwner(entity);
        if (entity.getRelease() != null && !entityManager.contains(entity.getRelease())) {
            entity.setRelease(releaseRepository.findById(entity.getRelease().getId()));
        }else if (entity.getWork() != null && !entityManager.contains(entity.getWork())) {
            entity.setWork(workRepository.findById(entity.getWork().getId()));
        }else if (entity.getRecording() != null && !entityManager.contains(entity.getRecording())) {
            entity.setRecording(recordingRepository.findById(entity.getRecording().getId()));
        }else if (entity.getRecordingSession() != null && !entityManager.contains(entity.getRecordingSession())) {
            entity.setRecordingSession(recordingSessionRepository.findById(entity.getRecordingSession().getId()));
        }
        ContributorEntity contributor = super.merge(entity);
        return contributor;
    }

    @Override
    public void remove(ContributorEntity entity) {
        if(Hibernate.isInitialized(entity.getRelease()) && entity.getRelease() != null) {
            ((ContributorOwner)entity.getRelease()).removeContributor(entity);
        }else if(Hibernate.isInitialized(entity.getWork()) && entity.getWork()!=null) {
            ((ContributorOwner)entity.getWork()).removeContributor(entity);
        }else if(Hibernate.isInitialized(entity.getRecording()) && entity.getRecording()!=null) {
            ((ContributorOwner)entity.getRecording()).removeContributor(entity);
        }else if(Hibernate.isInitialized(entity.getRecordingSession()) && entity.getRecordingSession()!=null) {
            ((ContributorOwner)entity.getRecordingSession()).removeContributor(entity);
        }
        super.remove(entity);
    }

    private void initializeOwner(ContributorEntity entity) {
        if (entity.getOwner()!=null) {
            if(SMDIdentityReferenceEntity.typeForClass(entity.getOwner().getClass()).equals(SMDIdentityReferenceEntity.typeForClass(ReleaseEntity.class)) && entity.getRelease()==null) {
                entity.setRelease(releaseRepository.findById(entity.getOwner().getId()));
            }else if(SMDIdentityReferenceEntity.typeForClass(entity.getOwner().getClass()).equals(SMDIdentityReferenceEntity.typeForClass(WorkEntity.class)) && entity.getWork()==null) {
                entity.setWork(workRepository.findById(entity.getOwner().getId()));
            }else if(SMDIdentityReferenceEntity.typeForClass(entity.getOwner().getClass()).equals(SMDIdentityReferenceEntity.typeForClass(RecordingEntity.class)) && entity.getRecording()==null) {
                entity.setRecording(recordingRepository.findById(entity.getOwner().getId()));
            }else if(SMDIdentityReferenceEntity.typeForClass(entity.getOwner().getClass()).equals(SMDIdentityReferenceEntity.typeForClass(RecordingSessionEntity.class)) && entity.getRecordingSession()==null) {
                entity.setRecordingSession(recordingSessionRepository.findById(entity.getOwner().getId()));
            }
        }
    }
}
