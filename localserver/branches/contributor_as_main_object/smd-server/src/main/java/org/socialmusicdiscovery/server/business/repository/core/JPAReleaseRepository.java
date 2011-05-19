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
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPAReleaseRepository extends AbstractJPASMDIdentityRepository<ReleaseEntity> implements ReleaseRepository {
    private ContributorRepository contributorRepository;
    private TrackRepository trackRepository;
    private MediumRepository mediumRepository;
    private LabelRepository labelRepository;

    @Inject
    public JPAReleaseRepository(EntityManager em, ContributorRepository contributorRepository, TrackRepository trackRepository, MediumRepository mediumRepository, LabelRepository labelRepository) {
        super(em);
        this.contributorRepository = contributorRepository;
        this.trackRepository = trackRepository;
        this.mediumRepository = mediumRepository;
        this.labelRepository = labelRepository;
    }

    public Collection<ReleaseEntity> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<ReleaseEntity> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.name)=:name order by e.name");
        query.setParameter("name", name.toLowerCase());
        return query.getResultList();
    }

    public Collection<ReleaseEntity> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.name) like :name order by e.name");
        query.setParameter("name", "%" + name.toLowerCase() + "%");
        return query.getResultList();
    }

    public Collection<ReleaseEntity> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(recordingQueryStringFor("e", "release", "artist", "relation", "searchRelation", mandatoryRelations, optionalRelations, true) + " WHERE searchRelation.reference=:artist order by e.name");
        query.setParameter("artist", artistId);
        return query.getResultList();
    }

    public Collection<ReleaseEntity> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(recordingQueryStringFor("e", "release", "work", "relation", "searchRelation", mandatoryRelations, optionalRelations, true) + " WHERE searchRelation.reference=:work order by e.name");
        query.setParameter("work", workId);
        return query.getResultList();
    }

    @Override
    public void create(ReleaseEntity entity) {
        if (entity.getLabel() != null && !entityManager.contains(entity.getLabel())) {
            entity.setLabel(labelRepository.findById(entity.getLabel().getId()));
        }
        for (Medium medium : entity.getMediums()) {
            if(!entityManager.contains(medium)) {
                if(((MediumEntity)medium).getLastUpdated()==null) {
                    ((MediumEntity)medium).setLastUpdated(entity.getLastUpdated());
                }
                if(((MediumEntity)medium).getLastUpdatedBy()==null) {
                    ((MediumEntity)medium).setLastUpdatedBy(entity.getLastUpdatedBy());
                }
                if(((MediumEntity)medium).getReference()==null || entity.getReference().getId() == null) {
                    ((MediumEntity)medium).setReference(SMDIdentityReferenceEntity.forEntity(medium));
                }
                ((MediumEntity) medium).setRelease(entity);
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
    public ReleaseEntity merge(ReleaseEntity entity) {
        if (entity.getLabel() != null && !entityManager.contains(entity.getLabel())) {
            entity.setLabel(labelRepository.findById(entity.getLabel().getId()));
        }
        for (Medium medium : entity.getMediums()) {
            if(!entityManager.contains(medium)) {
                if(((MediumEntity)medium).getLastUpdated()==null) {
                    ((MediumEntity)medium).setLastUpdated(entity.getLastUpdated());
                }
                if(((MediumEntity)medium).getLastUpdatedBy()==null) {
                    ((MediumEntity)medium).setLastUpdatedBy(entity.getLastUpdatedBy());
                }
                ((MediumEntity) medium).setRelease(entity);
                mediumRepository.merge((MediumEntity) medium);
            }
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
        return super.merge(entity);
    }

    @Override
    public void remove(ReleaseEntity entity) {
        entityManager.refresh(entity);
        for (Track track : entity.getTracks()) {
            track.setRelease(null);
            trackRepository.remove((TrackEntity)track);
        }
        entity.getSearchRelations().clear();
        entityManager.createQuery("DELETE from RecordingReleaseSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();

        entityManager.createNativeQuery("DELETE from classification_references where reference_to_id=:id").setParameter("id",entity.getId()).executeUpdate();
        super.remove(entity);
    }

}
