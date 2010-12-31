package org.socialmusicdiscovery.server.business.logic;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.api.mediaimport.PostProcessor;
import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.model.search.*;
import org.socialmusicdiscovery.server.business.repository.classification.ClassificationRepository;
import org.socialmusicdiscovery.server.business.repository.core.ContributorRepository;
import org.socialmusicdiscovery.server.business.repository.core.RecordingSessionRepository;
import org.socialmusicdiscovery.server.business.repository.core.ReleaseRepository;
import org.socialmusicdiscovery.server.business.repository.core.TrackRepository;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SearchRelationPostProcessor implements PostProcessor {
    @Inject
    private EntityManager entityManager;

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

    private boolean abort = false;

    public SearchRelationPostProcessor() {
        InjectHelper.injectMembers(this);
    }

    public String getId() {
        return "searchrelations";
    }

    public void abort() {
        this.abort = true;
    }

    public void execute(ProcessingStatusCallback progressHandler) {
        abort = false;
        entityManager.getTransaction().begin();
        entityManager.clear();
        entityManager.createQuery("DELETE from ReleaseSearchRelationEntity").executeUpdate();
        entityManager.createQuery("DELETE from RecordingSearchRelationEntity").executeUpdate();
        entityManager.createQuery("DELETE from WorkSearchRelationEntity").executeUpdate();
        entityManager.createQuery("DELETE from PersonSearchRelationEntity").executeUpdate();
        entityManager.createQuery("DELETE from ArtistSearchRelationEntity").executeUpdate();
        entityManager.createQuery("DELETE from TrackSearchRelationEntity").executeUpdate();
        entityManager.createQuery("DELETE from ClassificationSearchRelationEntity").executeUpdate();
        entityManager.getTransaction().commit();

        entityManager.clear();
        entityManager.getTransaction().begin();
        Collection<ReleaseEntity> releases = releaseRepository.findAllWithRelations(null, Arrays.asList("label"));
        entityManager.getTransaction().commit();
        entityManager.clear();

        long i = 0;
        for (Release release : releases) {
            entityManager.getTransaction().begin();
            i++;
            progressHandler.progress(getId(), release.getName(), i, (long) releases.size());
            Collection<ContributorEntity> releaseContributors = contributorRepository.findByReleaseWithRelations(release.getId(), Arrays.asList("artist"), null);
            Collection<ClassificationEntity> releaseClassifications = classificationRepository.findByReference(release.getId());
            Set<ReleaseSearchRelationEntity> releaseSearchRelations = new HashSet<ReleaseSearchRelationEntity>();
            Collection<TrackEntity> tracks = trackRepository.findByReleaseWithRelations(release.getId(), Arrays.asList("recording"), null);
            Set<Contributor> aggregatedContributors = new HashSet<Contributor>(releaseContributors);
            Set<Classification> aggregatedClassifications = new HashSet<Classification>(releaseClassifications);
            Set<Recording> aggregatedRecordings = new HashSet<Recording>();
            Set<Work> aggregatedWorks = new HashSet<Work>();
            Set<Recording> handledRecordings = new HashSet<Recording>();
            for (Track track : tracks) {
                Recording recording = track.getRecording();
                handledRecordings.add(recording);
                addRecording(aggregatedContributors, aggregatedRecordings, aggregatedWorks, aggregatedClassifications, releaseContributors, releaseClassifications, release, recording, track);
                releaseSearchRelations.add(new ReleaseSearchRelationEntity(release.getId(), track));
            }

            Collection<RecordingSessionEntity> recordingSessions = recordingSessionRepository.findByReleaseWithRelations(release.getId(), Arrays.asList("recordings"), null);
            for (RecordingSession session : recordingSessions) {
                for (Recording recording : session.getRecordings()) {
                    // We only need to handle this if we haven't already taken care of this recording
                    if (!handledRecordings.contains(recording)) {
                        addRecording(aggregatedContributors, aggregatedRecordings, aggregatedWorks, aggregatedClassifications, releaseContributors, releaseClassifications, release, recording, null);
                    }
                }
            }

            // Add relations for all contributors related to either a recording, work which is part of this release or related to the release itself
            for (Contributor contributor : aggregatedContributors) {
                addContributor(releaseSearchRelations, contributor, release, ReleaseSearchRelationEntity.class);
            }
            // Add relations for all classifications related to either a track or, work which is part of this release or related to the release itself
            for (Classification classification : aggregatedClassifications) {
                releaseSearchRelations.add(new ReleaseSearchRelationEntity(release.getId(), SMDIdentityReferenceEntity.typeForClass(classification.getClass()), classification.getId(), classification.getType()));
                entityManager.persist(new ClassificationSearchRelationEntity(classification.getId(), release));
            }

            for (Work work : aggregatedWorks) {
                releaseSearchRelations.add(new ReleaseSearchRelationEntity(release.getId(), work));
            }
            for (Recording recording : aggregatedRecordings) {
                releaseSearchRelations.add(new ReleaseSearchRelationEntity(release.getId(), recording));
            }
            for (ReleaseSearchRelationEntity relation : releaseSearchRelations) {
                entityManager.persist(relation);
            }

            entityManager.getTransaction().commit();
            entityManager.clear();
            if (abort) {
                break;
            }
        }
        if (abort) {
            progressHandler.aborted(getId());
        } else {
            progressHandler.finished(getId());
        }
    }

    private void addRecording(Set<Contributor> aggregatedContributors, Set<Recording> aggregatedRecordings, Set<Work> aggregatedWorks, Set<Classification> aggregatedClassifications, Collection<ContributorEntity> releaseContributors, Collection<ClassificationEntity> releaseClassifications, Release release, Recording recording, Track track) {
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

        Set<TrackSearchRelationEntity> trackSearchRelations = new HashSet<TrackSearchRelationEntity>();
        Set<Contributor> aggregatedTrackContributors = new HashSet<Contributor>(recording.getContributors());
        Set<RecordingSearchRelationEntity> recordingSearchRelations = new HashSet<RecordingSearchRelationEntity>();
        Set<Contributor> recordingContributors = recording.getContributors();
        aggregatedContributors.addAll(recordingContributors);
        aggregatedContributors.addAll(sessionContributors);
        aggregatedRecordings.add(recording);
        Work work = recording.getWork();
        Set<Contributor> aggregatedWorkContributors = new HashSet<Contributor>();
        Set<ClassificationEntity> aggregatedWorkClassifications = new HashSet<ClassificationEntity>();
        Set<Work> works = new HashSet<Work>();
        while (work != null) {
            works.add(work);
            Set<WorkSearchRelationEntity> workSearchRelations = new HashSet<WorkSearchRelationEntity>();
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
                    addContributor(workSearchRelations, contributor, workOrPart, WorkSearchRelationEntity.class);
                }
            }

            // Add relations for contributors related to a recording session which contains this work
            // If there are contributors of the same role defined directly on the recording or work, the contributors on the recording session with this role is ignored
            for (Contributor contributor : sessionContributors) {
                if (!containsRole(workContributors, contributor.getType()) &&
                        !containsRole(recordingContributors, contributor.getType())) {
                    for (Work workOrPart : works) {
                        addContributor(workSearchRelations, contributor, workOrPart, WorkSearchRelationEntity.class);
                    }
                }
            }

            // Add relations for contributors related to a recording which contains this work
            // If there are contributors of the same role defined directly on the work, the contributors on the recording with this role is ignored
            for (Contributor contributor : recordingContributors) {
                if (!containsRole(workContributors, contributor.getType())) {
                    addContributor(workSearchRelations, contributor, work, WorkSearchRelationEntity.class);
                }
            }

            // Add relations for contributors related to a release which contains this work
            // If there are contributors of the same role defined directly on the recording, session or work, the contributors on the release with this role is ignored
            for (Contributor contributor : releaseContributors) {
                if (!containsRole(workContributors, contributor.getType()) &&
                        !containsRole(recordingContributors, contributor.getType()) &&
                        !containsRole(sessionContributors, contributor.getType())) {
                    addContributor(workSearchRelations, contributor, work, WorkSearchRelationEntity.class);
                }
            }

            for (ClassificationEntity classification : workClassifications) {
                for (Work workOrPart : works) {
                    workSearchRelations.add(new WorkSearchRelationEntity(workOrPart.getId(), SMDIdentityReferenceEntity.typeForClass(classification.getClass()), classification.getId(), classification.getType()));
                    // Need to use merge because a work can be part of multiple releases
                    entityManager.merge(new ClassificationSearchRelationEntity(classification.getId(), workOrPart));
                }
            }
            for (ClassificationEntity classification : recordingClassifications) {
                for (Work workOrPart : works) {
                    workSearchRelations.add(new WorkSearchRelationEntity(workOrPart.getId(), SMDIdentityReferenceEntity.typeForClass(classification.getClass()), classification.getId(), classification.getType()));
                    // Need to use merge because a work can be part of multiple releases
                    entityManager.merge(new ClassificationSearchRelationEntity(classification.getId(), workOrPart));
                }
            }

            for (ClassificationEntity classification : releaseClassifications) {
                // Only add release classifications if it hasn't been defined on the recording or work
                if (!containsType(recordingClassifications, classification.getType()) &&
                        !containsType(workClassifications, classification.getType())) {
                    for (Work workOrPart : works) {
                        workSearchRelations.add(new WorkSearchRelationEntity(workOrPart.getId(), SMDIdentityReferenceEntity.typeForClass(classification.getClass()), classification.getId(), classification.getType()));
                        // Need to use merge because a work can be part of multiple releases
                        entityManager.merge(new ClassificationSearchRelationEntity(classification.getId(), workOrPart));
                    }
                }
            }

            if (release != null) {
                workSearchRelations.add(new WorkSearchRelationEntity(work.getId(), release));
                if (release.getLabel() != null) {
                    workSearchRelations.add(new WorkSearchRelationEntity(work.getId(), release.getLabel()));
                }
            }
            workSearchRelations.add(new WorkSearchRelationEntity(work.getId(), recording));
            recordingSearchRelations.add(new RecordingSearchRelationEntity(recording.getId(), work));
            if (track != null) {
                workSearchRelations.add(new WorkSearchRelationEntity(work.getId(), track));
                entityManager.persist(new TrackSearchRelationEntity(track.getId(), work));
            }
            for (WorkSearchRelationEntity relation : workSearchRelations) {
                // Need to use merge because a work can be part of multiple releases
                entityManager.merge(relation);
            }
            work = work.getParent();
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
            addContributor(recordingSearchRelations, contributor, recording, RecordingSearchRelationEntity.class);
            if (track != null) {
                addContributor(trackSearchRelations, contributor, track, TrackSearchRelationEntity.class);
            }
        }

        // Add relations for contributors related to a recording session which this recording is part of
        // If there are contributors of the same role defined directly on the recording, the contributors on the release with this role is ignored.
        for (Contributor contributor : sessionContributors) {
            if (!containsRole(recordingContributors, contributor.getType())) {
                aggregatedTrackContributors.add(contributor);
                addContributor(recordingSearchRelations, contributor, recording, RecordingSearchRelationEntity.class);
                if (track != null) {
                    addContributor(trackSearchRelations, contributor, track, TrackSearchRelationEntity.class);
                }
            }
        }

        // Add relations for contributors related to a release which this recording is part of
        // If there are contributors of the same role defined directly on the recording, the contributors on the release with this role is ignored.
        for (Contributor contributor : releaseContributors) {
            if (!containsRole(recordingContributors, contributor.getType())) {
                aggregatedTrackContributors.add(contributor);
                addContributor(recordingSearchRelations, contributor, recording, RecordingSearchRelationEntity.class);
                if (track != null) {
                    addContributor(trackSearchRelations, contributor, track, TrackSearchRelationEntity.class);
                }
            }
        }

        // Add relations for contributors related to the work which this recording represent
        for (Contributor contributor : aggregatedWorkContributors) {
            addContributor(recordingSearchRelations, contributor, recording, RecordingSearchRelationEntity.class);
            if (track != null) {
                addContributor(trackSearchRelations, contributor, track, TrackSearchRelationEntity.class);
            }
        }

        for (Contributor contributor1 : aggregatedTrackContributors) {
            // Create artist search relations between artists that performs or contributes on the same recording
            for (Contributor contributor2 : aggregatedTrackContributors) {
                if (!contributor1.equals(contributor2) && !contributor1.getType().equals(contributor2.getType())) {
                    // Need to use merge because an artist can be part of multiple releases
                    entityManager.merge(new ArtistSearchRelationEntity(contributor1.getArtist().getId(), SMDIdentityReferenceEntity.typeForClass(contributor2.getArtist().getClass()), contributor2.getArtist().getId(), contributor2.getType()));
                }
            }

            // Create artist search relations to classification
            for (ClassificationEntity classification : aggregatedTrackClassifications) {
                // Need to use merge because an artist can be part of multiple releases
                entityManager.merge(new ArtistSearchRelationEntity(contributor1.getArtist().getId(), SMDIdentityReferenceEntity.typeForClass(classification.getClass()), classification.getId(), contributor1.getType()));
                entityManager.merge(new ClassificationSearchRelationEntity(classification.getId(), SMDIdentityReferenceEntity.typeForClass(contributor1.getArtist().getClass()), contributor1.getArtist().getId(), contributor1.getType()));
            }
        }
        for (ClassificationEntity classification : aggregatedTrackClassifications) {
            // Need to use merge because a recording can be part of multiple releases
            entityManager.merge(new ClassificationSearchRelationEntity(classification.getId(), recording));
            if (release != null) {
                if (track != null) {
                    entityManager.persist(new ClassificationSearchRelationEntity(classification.getId(), track));
                }
                if (release.getLabel() != null) {
                    // Need to use merge because a label can be part of multiple releases
                    entityManager.merge(new ClassificationSearchRelationEntity(classification.getId(), release.getLabel()));
                }
            }
        }
        if (release != null) {
            if (release.getLabel() != null) {
                recordingSearchRelations.add(new RecordingSearchRelationEntity(recording.getId(), release.getLabel()));
                if (track != null) {
                    trackSearchRelations.add(new TrackSearchRelationEntity(track.getId(), release.getLabel()));
                }
            }
            recordingSearchRelations.add(new RecordingSearchRelationEntity(recording.getId(), release));
            if (track != null) {
                trackSearchRelations.add(new TrackSearchRelationEntity(track.getId(), release));
            }
        }
        if (track != null) {
            recordingSearchRelations.add(new RecordingSearchRelationEntity(recording.getId(), track));
            trackSearchRelations.add(new TrackSearchRelationEntity(track.getId(), recording));
            for (ClassificationEntity classification : aggregatedTrackClassifications) {
                trackSearchRelations.add(new TrackSearchRelationEntity(track.getId(), SMDIdentityReferenceEntity.typeForClass(classification.getClass()), classification.getId(), classification.getType()));
            }
        }
        for (ClassificationEntity classification : aggregatedTrackClassifications) {
            recordingSearchRelations.add(new RecordingSearchRelationEntity(recording.getId(), SMDIdentityReferenceEntity.typeForClass(classification.getClass()), classification.getId(), classification.getType()));
        }

        for (RecordingSearchRelationEntity relation : recordingSearchRelations) {
            // Need to use merge because a recording can be part of multiple releases
            entityManager.merge(relation);
        }
        for (TrackSearchRelationEntity relation : trackSearchRelations) {
            entityManager.persist(relation);
        }
    }

    private <T extends SearchRelationEntity> void addContributor(Set<T> relations, Contributor contributor, SMDIdentity owner, Class<T> relationClass) {
        try {
            Artist artist = contributor.getArtist();
            T relation = relationClass.newInstance();
            relation.setId(owner.getId());
            relation.setReferenceType(SMDIdentityReferenceEntity.typeForClass(artist.getClass()));
            relation.setType(contributor.getType());
            relation.setReference(artist.getId());
            relations.add(relation);

            // Need to use merge because an artist can be part of multiple releases
            entityManager.merge(new ArtistSearchRelationEntity(artist.getId(), SMDIdentityReferenceEntity.typeForClass(owner.getClass()), owner.getId(), contributor.getType()));

            Person person = contributor.getArtist().getPerson();
            if (person != null) {
                relation = relationClass.newInstance();
                relation.setId(owner.getId());
                relation.setReference(person);
                relations.add(relation);
                // Need to use merge because a person can be part of multiple releases
                entityManager.merge(new PersonSearchRelationEntity(person.getId(), owner));
                entityManager.merge(new PersonSearchRelationEntity(person.getId(), SMDIdentityReferenceEntity.typeForClass(artist.getClass()), artist.getId(), contributor.getType()));
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
