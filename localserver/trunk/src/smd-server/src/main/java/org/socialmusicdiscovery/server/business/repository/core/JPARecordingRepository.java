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
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationReferenceEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.model.search.*;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

public class JPARecordingRepository extends AbstractJPASMDIdentityRepository<RecordingEntity> implements RecordingRepository {
    ContributorRepository contributorRepository;
    WorkRepository workRepository;
    TrackRepository trackRepository;

    @Inject
    public JPARecordingRepository(EntityManager em, ContributorRepository contributorRepository, WorkRepository workRepository, TrackRepository trackRepository) {
        super(em);
        this.contributorRepository = contributorRepository;
        this.workRepository = workRepository;
        this.trackRepository = trackRepository;
    }

    public Collection<RecordingEntity> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<RecordingEntity> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query;
        if(mandatoryRelations.contains("works") || optionalRelations.contains("works")) {
            query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" where lower(e.name)=:name or lower(works.name)=:name");
        }else {
            query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" where lower(e.name)=:name");
        }
        query.setParameter("name",name.toLowerCase());
        return query.getResultList();
    }

    public Collection<RecordingEntity> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" JOIN e.works as works WHERE works.id=:work");
        query.setParameter("work",workId);
        return query.getResultList();
    }

    public Collection<RecordingEntity> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query;
        if(mandatoryRelations.contains("works") || optionalRelations.contains("works")) {
            query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" where lower(e.name) like :name or lower(works.name) like :name");
        }else {
            query = entityManager.createQuery(queryStringFor("e",mandatoryRelations, optionalRelations)+" where lower(e.name) like :name");
        }
        query.setParameter("name","%"+name.toLowerCase()+"%");
        return query.getResultList();
    }

    @Override
    public Collection<RecordingEntity> findBySearchRelation(SMDIdentity entity) {
        Query query = null;
        String type = SMDIdentityReferenceEntity.forEntity(entity).getType();
        if(type.equals(SMDIdentityReferenceEntity.typeForClass(LabelEntity.class))) {
            query = entityManager.createQuery("select distinct e from RecordingEntity as e JOIN e.labelSearchRelations as relations where relations.reference=:reference");
            query.setParameter("reference",entity.getId());
        }else if(type.equals(SMDIdentityReferenceEntity.typeForClass(ReleaseEntity.class))) {
            query = entityManager.createQuery("select distinct e from RecordingEntity as e JOIN e.releaseSearchRelations as relations where relations.reference=:reference");
            query.setParameter("reference",entity.getId());
        }else if(type.equals(SMDIdentityReferenceEntity.typeForClass(TrackEntity.class))) {
            query = entityManager.createQuery("select distinct e from RecordingEntity as e JOIN e.trackSearchRelations as relations where relations.reference=:reference");
            query.setParameter("reference",entity.getId());
        }else if(type.equals(SMDIdentityReferenceEntity.typeForClass(WorkEntity.class))) {
            query = entityManager.createQuery("select distinct e from RecordingEntity as e JOIN e.workSearchRelations as relations where relations.reference=:reference");
            query.setParameter("reference",entity.getId());
        }else if(type.equals(SMDIdentityReferenceEntity.typeForClass(ContributorEntity.class))) {
            query = entityManager.createQuery("select distinct e from RecordingEntity as e JOIN e.artistSearchRelations as relations where relations.reference=:reference");
            query.setParameter("reference",((Contributor)entity).getArtist().getId());
        }else if(type.equals(SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class))) {
            query = entityManager.createQuery("select distinct e from RecordingEntity as e JOIN e.artistSearchRelations as relations where relations.reference=:reference");
            query.setParameter("reference",entity.getId());
        }else if(type.equals(SMDIdentityReferenceEntity.typeForClass(ClassificationReferenceEntity.class))) {
            query = entityManager.createQuery("select distinct e from RecordingEntity as e JOIN e.classificationSearchRelations as relations where relations.reference=:reference");
            query.setParameter("reference",((ClassificationReferenceEntity)entity).getClassification().getId());
        }else if(type.equals(SMDIdentityReferenceEntity.typeForClass(ClassificationEntity.class))) {
            query = entityManager.createQuery("select distinct e from RecordingEntity as e JOIN e.classificationSearchRelations as relations where relations.reference=:reference");
            query.setParameter("reference",entity.getId());
        }
        if(query!=null) {
            return query.getResultList();
        }else {
            return new ArrayList<RecordingEntity>();
        }

    }

    @Override
    public void create(RecordingEntity entity) {
        if (entity.getMixOf() != null && !entityManager.contains(entity.getMixOf())) {
            entity.setMixOf(findById(entity.getMixOf().getId()));
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
    public RecordingEntity merge(RecordingEntity entity) {
        if (entity.getMixOf() != null && !entityManager.contains(entity.getMixOf())) {
            entity.setMixOf(findById(entity.getMixOf().getId()));
        }
        if(entity.getSortAs()==null) {
            entity.setSortAsAutomatically();
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
    public void remove(RecordingEntity entity) {
        entity.getLabelSearchRelations().clear();
        entity.getReleaseSearchRelations().clear();
        entity.getTrackSearchRelations().clear();
        entity.getWorkSearchRelations().clear();
        entity.getArtistSearchRelations().clear();
        entity.getClassificationSearchRelations().clear();
        entityManager.createQuery("DELETE from ReleaseSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();

        entityManager.createNativeQuery("DELETE from classification_references where reference_to_id=:id").setParameter("id",entity.getId()).executeUpdate();
        super.remove(entity);
    }

    public void refresh(RecordingEntity entity) {
        entityManager.flush();
        entityManager.refresh(entity);

        Set<Work> works = entity.getWorks();
        Set<Work> aggregatedWorks = new HashSet<Work>(works);
        Set<Contributor> aggregatedContributors = new HashSet<Contributor>(entity.getContributors());
        Set<Contributor> aggregatedWorkContributors = new HashSet<Contributor>();

        for (Work work : works) {
            while(work != null) {
                aggregatedWorkContributors.addAll(work.getContributors());
                aggregatedContributors.addAll(work.getContributors());
                aggregatedWorks.add(work);
                if(work instanceof Part) {
                    work = ((Part)work).getParent();
                }else {
                    work = null;
                }
            }
        }

        if(entity.getRecordingSession()!=null) {
            RecordingSession session = entity.getRecordingSession();
            for (Contributor contributor : session.getContributors()) {
                if (!containsRole(aggregatedWorkContributors, contributor.getType()) &&
                        !containsRole(entity.getContributors(), contributor.getType())) {
                    aggregatedContributors.add(contributor);
                }
            }
        }

        Collection<TrackEntity> tracks = trackRepository.findByRecordingWithRelations(entity.getId(),null, null);
        Set<Release> releases = new HashSet<Release>();
        Set<Label> labels = new HashSet<Label>();

        for (TrackEntity track : tracks) {
            if(track.getRelease()!=null) {
                Release release = track.getRelease();
                releases.add(release);
                for (Contributor contributor : release.getContributors()) {
                    if (!containsRole(aggregatedContributors, contributor.getType())) {
                        aggregatedContributors.add(contributor);
                    }
                }
            }
        }

        synchronizeSearchRelations(entity, RecordingArtistSearchRelationEntity.class, entity.getArtistSearchRelations(), aggregatedContributors);
        synchronizeSearchRelations(entity, RecordingLabelSearchRelationEntity.class, entity.getLabelSearchRelations(), labels);
        synchronizeSearchRelations(entity, RecordingTrackSearchRelationEntity.class, entity.getTrackSearchRelations(), new HashSet<TrackEntity>(tracks));
        synchronizeSearchRelations(entity, RecordingReleaseSearchRelationEntity.class, entity.getReleaseSearchRelations(), releases);
        synchronizeSearchRelations(entity, RecordingWorkSearchRelationEntity.class, entity.getWorkSearchRelations(), aggregatedWorks);
        entityManager.flush();
    }

    /**
     * Check if the list of contributors contain a contributor of the specified type
     *
     * @param contributors List of contributors to check
     * @param type         Type of contributor to check for
     * @return true if the list of contributors contains the specified role
     */
    private boolean containsRole(Collection<Contributor> contributors, String type) {
        for (Contributor contributor : contributors) {
            if (contributor.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    private <T extends RecordingSearchRelationEntity> void synchronizeSearchRelations(Recording owner, Class<T> relationClass, Set<T> searchRelations, Set<? extends SMDIdentity> entities) {
        // Remove old relations which no longer exists
        Map<String, ? extends SMDIdentity> newEntitiesIds = referenceMap(entities);
        Set<T> previousSearchRelations = new HashSet<T>(searchRelations);
        for (T searchRelation : previousSearchRelations) {
            if(!newEntitiesIds.containsKey(reference(searchRelation))) {
                searchRelations.remove(searchRelation);
            }
        }

        // Add new relations
        Map<String, ? extends SearchRelationEntity> currentEntitiesIds = relationReferenceMap(searchRelations);
        for (Map.Entry<String, ? extends SMDIdentity> entry : newEntitiesIds.entrySet()) {
            if(!currentEntitiesIds.containsKey(entry.getKey())) {
                try {
                    T relation = relationClass.newInstance();
                    relation.setId(owner.getId());
                    if(entry.getValue() instanceof ContributorEntity) {
                        relation.setReference(((Contributor)entry.getValue()).getArtist().getId());
                        relation.setType(((Contributor)entry.getValue()).getType());
                        relation.setReferenceType(SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class));
                    }else {
                        relation.setReference(entry.getValue().getId());
                        relation.setReferenceType(SMDIdentityReferenceEntity.forEntity(entry.getValue()).getType());
                    }
                    searchRelations.add(relation);
                } catch (InstantiationException e) {
                    // TODO: Throw exception ?
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO: Throw exception ?
                    e.printStackTrace();
                }
            }
        }
    }

    private String reference(RecordingSearchRelationEntity relation) {
        String type = relation.getType();
        if(type!=null) {
            return type+relation.getReference();
        }else {
            return relation.getReference();
        }
    }
    private <T extends SMDIdentity> Map<String, T> referenceMap(Set<T> entities) {
        Map<String, T> result = new HashMap<String, T>(entities.size());
        for (T entity : entities) {
            if(entity instanceof Contributor) {
                result.put(((Contributor)entity).getType()+((Contributor)entity).getArtist().getId(), entity);
            }else {
                result.put(entity.getId(), entity);
            }
        }
        return result;
    }

    private <T extends SearchRelationEntity> Map<String, T> relationReferenceMap(Set<T> entities) {
        Map<String, T> result = new HashMap<String, T>(entities.size());
        for (T entity : entities) {
            if(entity instanceof RecordingArtistSearchRelationEntity) {
                result.put(entity.getType()+entity.getReference(), entity);
            }else {
                result.put(entity.getReference(), entity);
            }
        }
        return result;
    }
}
