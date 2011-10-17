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

package org.socialmusicdiscovery.server.business.logic;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.api.mediaimport.AbstractProcessingModule;
import org.socialmusicdiscovery.server.api.mediaimport.PostProcessor;
import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.model.search.*;
import org.socialmusicdiscovery.server.business.repository.classification.ClassificationRepository;
import org.socialmusicdiscovery.server.business.repository.core.ContributorRepository;
import org.socialmusicdiscovery.server.business.repository.core.RecordingSessionRepository;
import org.socialmusicdiscovery.server.business.repository.core.ReleaseRepository;
import org.socialmusicdiscovery.server.business.repository.core.TrackRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This is a post processor action which runs after each {@link org.socialmusicdiscovery.server.api.mediaimport.MediaImporter} and goes
 * through all changes and creates, updates or removes the {@link SearchRelationEntity} entities affected
 */
public class SearchRelationPostProcessor extends AbstractProcessingModule implements PostProcessor {
    @Inject
    private ReleaseRepository releaseRepository;

    @Inject
    private ContributorRepository contributorRepository;

    @Inject
    private TrackRepository trackRepository;

    @Inject
    private RecordingSessionRepository recordingSessionRepository;

    @Inject
    private ClassificationRepository classificationRepository;

    public String getId() {
        return "searchrelations";
    }

    public void execute(ProcessingStatusCallback progressHandler) {
        // Start by dropping all existing search relations
        entityManager.getTransaction().begin();
        entityManager.clear();
        entityManager.createQuery("DELETE from ReleaseSearchRelationEntity").executeUpdate();
        entityManager.createQuery("DELETE from RecordingLabelSearchRelationEntity").executeUpdate();
        entityManager.createQuery("DELETE from RecordingReleaseSearchRelationEntity").executeUpdate();
        entityManager.createQuery("DELETE from RecordingTrackSearchRelationEntity").executeUpdate();
        entityManager.createQuery("DELETE from RecordingWorkSearchRelationEntity").executeUpdate();
        entityManager.createQuery("DELETE from RecordingArtistSearchRelationEntity").executeUpdate();
        entityManager.createQuery("DELETE from RecordingClassificationSearchRelationEntity").executeUpdate();
        entityManager.createQuery("DELETE from PersonSearchRelationEntity").executeUpdate();
        entityManager.getTransaction().commit();

        // Get all available releases
        entityManager.clear();
        entityManager.getTransaction().begin();
        Collection<ReleaseEntity> releases = releaseRepository.findAllWithRelations(null, Arrays.asList("label"));
        entityManager.getTransaction().commit();
        entityManager.clear();

        // Iterate through each release
        long i = 0;
        for (Release release : releases) {
            entityManager.getTransaction().begin();
            i++;
            progressHandler.progress(getId(), release.getName(), i, (long) releases.size());
            Collection<ContributorEntity> releaseContributors = contributorRepository.findByReleaseWithRelations(release.getId(), Arrays.asList("artist"), null);
            Collection<ClassificationEntity> releaseClassifications = classificationRepository.findByReference(release.getId());
            Set<SearchRelationEntity> releaseSearchRelations = new HashSet<SearchRelationEntity>();
            Collection<TrackEntity> tracks = trackRepository.findByReleaseWithRelations(release.getId(), Arrays.asList("recording"), null);
            Set<Contributor> aggregatedContributors = new HashSet<Contributor>(releaseContributors);
            Set<Classification> aggregatedClassifications = new HashSet<Classification>(releaseClassifications);
            Set<Recording> aggregatedRecordings = new HashSet<Recording>();
            Set<Work> aggregatedWorks = new HashSet<Work>();
            for (Track track : tracks) {
                Recording recording = track.getRecording();
                aggregatedRecordings.add(recording);
                addRecording(aggregatedContributors, aggregatedWorks, aggregatedClassifications, releaseContributors, releaseClassifications, release, recording, track);
                releaseSearchRelations.add(new ReleaseSearchRelationEntity(release, track));
            }

            Collection<RecordingSessionEntity> recordingSessions = recordingSessionRepository.findByReleaseWithRelations(release.getId(), Arrays.asList("recordings"), null);
            for (RecordingSession session : recordingSessions) {
                for (Recording recording : session.getRecordings()) {
                    // We only need to handle this if we haven't already taken care of this recording
                    if (aggregatedRecordings.add(recording)) {
                        addRecording(aggregatedContributors, aggregatedWorks, aggregatedClassifications, releaseContributors, releaseClassifications, release, recording, null);
                    }
                }
            }

            // Add relations for all contributors related to either a recording, work which is part of this release or related to the release itself
            for (Contributor contributor : aggregatedContributors) {
                addContributor(releaseSearchRelations, contributor, release, ReleaseSearchRelationEntity.class);
            }
            // Add relations for all classifications related to either a track or, work which is part of this release or related to the release itself
            for (Classification classification : aggregatedClassifications) {
                releaseSearchRelations.add(new ReleaseSearchRelationEntity(release, classification));
            }

            for (Work work : aggregatedWorks) {
                releaseSearchRelations.add(new ReleaseSearchRelationEntity(release, work));
            }
            for (Recording recording : aggregatedRecordings) {
                releaseSearchRelations.add(new ReleaseSearchRelationEntity(release, recording));
            }
            for (SearchRelationEntity relation : releaseSearchRelations) {
                entityManager.persist(relation);
            }

            entityManager.getTransaction().commit();
            entityManager.clear();
            if (isAborted()) {
                break;
            }
        }
        if (isAborted()) {
            progressHandler.aborted(getId());
        } else {
            progressHandler.finished(getId());
        }
    }

    /**
     * Add search relations for the specified recording, note that this method can be called multiple times for the same recording if the recording
     * is part of multiple releases or tracks
     * @param aggregatedContributors Aggregated list of contributors, any {@link Contributor} directly or indirectly related to this recording will be appended to this list
     * @param aggregatedWorks Aggregated list of works, any {@link Work} related to this recording will be appended to this list
     * @param aggregatedClassifications Aggregated list of classifications, any {@link Classification} directly or indirectly related to this recording will be appended to this list
     * @param releaseContributors Aggregated list of contributors directly tied to a {@link Release} related to the recording
     * @param releaseClassifications Aggregated list of classifications directly ties to a {@link Release} related to the recording
     * @param release The release which triggered this recording to be added
     * @param recording The recording to process
     * @param track The track which triggered this recording to be added
     */
    private void addRecording(Set<Contributor> aggregatedContributors, Set<Work> aggregatedWorks, Set<Classification> aggregatedClassifications, Collection<ContributorEntity> releaseContributors, Collection<ClassificationEntity> releaseClassifications, Release release, Recording recording, Track track) {
        // Get all contributors for a recording session which this recording is part of
        Set<Contributor> sessionContributors = new HashSet<Contributor>();
        Set<RecordingSession> recordingSessions = new HashSet<RecordingSession>();

        //TODO: Can we solve this without type casting ?
        RecordingSession session = ((RecordingEntity) recording).getRecordingSession();
        if (session != null) {
            sessionContributors.addAll(session.getContributors());
            recordingSessions.add(session);
        }

        Set<ClassificationEntity> aggregatedTrackClassifications = new HashSet<ClassificationEntity>();
        Collection<ClassificationEntity> recordingClassifications = classificationRepository.findByReference(recording.getId());
        aggregatedTrackClassifications.addAll(recordingClassifications);
        aggregatedClassifications.addAll(recordingClassifications);

        Set<SearchRelationEntity> trackSearchRelations = new HashSet<SearchRelationEntity>();
        Set<Contributor> aggregatedTrackContributors = new HashSet<Contributor>(recording.getContributors());
        Set<SearchRelationEntity> recordingSearchRelations = new HashSet<SearchRelationEntity>();
        Set<Contributor> recordingContributors = recording.getContributors();
        aggregatedContributors.addAll(recordingContributors);
        aggregatedContributors.addAll(sessionContributors);
        Set<Contributor> aggregatedWorkContributors = new HashSet<Contributor>();
        Set<ClassificationEntity> aggregatedWorkClassifications = new HashSet<ClassificationEntity>();
        Set<Work> works = new HashSet<Work>();
        for (Work work : recording.getWorks()) {
            while (work != null) {
                works.add(work);
                Set<SearchRelationEntity> workSearchRelations = new HashSet<SearchRelationEntity>();
                Set<Contributor> workContributors = work.getContributors();
                aggregatedWorkContributors.addAll(workContributors);
                aggregatedContributors.addAll(workContributors);
                aggregatedWorks.add(work);

                Collection<ClassificationEntity> workClassifications = classificationRepository.findByReference(work.getId());
                aggregatedClassifications.addAll(workClassifications);
                aggregatedWorkClassifications.addAll(workClassifications);

                // Add relations for contributors directly related to this work
                for (Contributor contributor : workContributors) {
                    for (Work workOrPart : works) {
                        addContributor(null, contributor, workOrPart, null);
                    }
                }

                // Add relations for contributors related to a recording session which contains this work
                // If there are contributors of the same role defined directly on the recording or work, the contributors on the recording session with this role is ignored
                for (Contributor contributor : sessionContributors) {
                    if (!containsRole(workContributors, contributor.getType()) &&
                            !containsRole(recordingContributors, contributor.getType())) {
                        for (Work workOrPart : works) {
                            addContributor(null, contributor, workOrPart, null);
                        }
                    }
                }

                // Add relations for contributors related to a recording which contains this work
                // If there are contributors of the same role defined directly on the work, the contributors on the recording with this role is ignored
                for (Contributor contributor : recordingContributors) {
                    if (!containsRole(workContributors, contributor.getType())) {
                        addContributor(null, contributor, work, null);
                    }
                }

                // Add relations for contributors related to a release which contains this work
                // If there are contributors of the same role defined directly on the recording, session or work, the contributors on the release with this role is ignored
                for (Contributor contributor : releaseContributors) {
                    if (!containsRole(workContributors, contributor.getType()) &&
                            !containsRole(recordingContributors, contributor.getType()) &&
                            !containsRole(sessionContributors, contributor.getType())) {
                        addContributor(null, contributor, work, null);
                    }
                }

                recordingSearchRelations.add(new RecordingWorkSearchRelationEntity(recording, work));
                for (SearchRelationEntity relation : workSearchRelations) {
                    // Need to use merge because a work can be part of multiple releases
                    entityManager.merge(relation);
                }
                if(work instanceof Part) {
                    work = ((Part)work).getParent();
                }else {
                    work = null;
                }
            }
        }
        aggregatedTrackContributors.addAll(aggregatedWorkContributors);

        for (ClassificationEntity classification : aggregatedWorkClassifications) {
            // Only use work classifications if they aren't already defined on the Recording
            if (!containsType(recordingClassifications, classification.getType())) {
                aggregatedTrackClassifications.add(classification);
            }
        }

        for (ClassificationEntity classification : releaseClassifications) {
            // Only add release classifications if the classification on the recording or work doesn't contain the same type of classification
            if (!containsType(recordingClassifications, classification.getType()) &&
                    !containsType(aggregatedWorkClassifications, classification.getType())) {
                aggregatedTrackClassifications.add(classification);
            }
        }

        // Add relations for contributors directly related to this recording
        for (Contributor contributor : recordingContributors) {
            addContributor(recordingSearchRelations, contributor, recording, RecordingArtistSearchRelationEntity.class);
            if (track != null) {
                addContributor(null, contributor, track, null);
            }
        }

        // Add relations for contributors related to a recording session which this recording is part of
        // If there are contributors of the same role defined directly on the recording, the contributors on the release with this role is ignored.
        for (Contributor contributor : sessionContributors) {
            if (!containsRole(recordingContributors, contributor.getType())) {
                aggregatedTrackContributors.add(contributor);
                addContributor(recordingSearchRelations, contributor, recording, RecordingArtistSearchRelationEntity.class);
                if (track != null) {
                    addContributor(null, contributor, track, null);
                }
            }
        }

        // Add relations for contributors related to a release which this recording is part of
        // If there are contributors of the same role defined directly on the recording, the contributors on the release with this role is ignored.
        for (Contributor contributor : releaseContributors) {
            if (!containsRole(recordingContributors, contributor.getType())) {
                aggregatedTrackContributors.add(contributor);
                addContributor(recordingSearchRelations, contributor, recording, RecordingArtistSearchRelationEntity.class);
                if (track != null) {
                    addContributor(null, contributor, track, null);
                }
            }
        }

        // Add relations for contributors related to the work which this recording represent
        for (Contributor contributor : aggregatedWorkContributors) {
            addContributor(recordingSearchRelations, contributor, recording, RecordingArtistSearchRelationEntity.class);
            if (track != null) {
                addContributor(null, contributor, track, null);
            }
        }

        if (release != null) {
            if (release.getLabel() != null) {
                recordingSearchRelations.add(new RecordingLabelSearchRelationEntity(recording, release.getLabel()));
            }
            recordingSearchRelations.add(new RecordingReleaseSearchRelationEntity(recording, release));
        }
        if (track != null) {
            recordingSearchRelations.add(new RecordingTrackSearchRelationEntity(recording, track));
        }
        for (ClassificationEntity classification : aggregatedTrackClassifications) {
            recordingSearchRelations.add(new RecordingClassificationSearchRelationEntity(recording, classification));
        }

        for (SearchRelationEntity relation : recordingSearchRelations) {
            // Need to use merge because a recording can be part of multiple releases
            entityManager.merge(relation);
        }
        for (SearchRelationEntity relation : trackSearchRelations) {
            entityManager.persist(relation);
        }
    }

    /**
     * Creates a search relation for the owner related to the specified contributor and if the artist tied to the contributor have a relation to a
     * {@link Person} this method will also create a search relation to the contributor from the person entity.
     * @param relations The list of search relations which the new relation should be added to
     * @param contributor The contributor which the search relation should be related to
     * @param owner The owner of the search relation
     * @param relationClass The search relation class which should be used when creating the search relation
     * @param <T> The search relation class which extends from {@link SearchRelationEntity}
     * TODO: Feels like this is called in too many places with relations==null, maybe we can improve performance by creating person relations at the end of each release ?
     */
    private <T extends SearchRelationEntity> void addContributor(Set<SearchRelationEntity> relations, Contributor contributor, SMDIdentity owner, Class<T> relationClass) {
        try {
            if(relations!=null) {
                T artistRelation = relationClass.newInstance();
                artistRelation.setId(owner.getId());
                artistRelation.setReference(contributor);
                relations.add(artistRelation);
            }

            Person person = contributor.getArtist().getPerson();
            if (person != null) {
                // Need to use merge because a person can be part of multiple releases
                entityManager.merge(new PersonSearchRelationEntity(person, contributor));
            }
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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

    /**
     * Check if the list of classifications contain a classification of the specified type
     *
     * @param classifications List of classifications to check
     * @param type            Type of classification to check for
     * @return true if the list of contributors contains the specified role
     */
    private boolean containsType(Collection<? extends Classification> classifications, String type) {
        for (Classification classification : classifications) {
            if (classification.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }
}
