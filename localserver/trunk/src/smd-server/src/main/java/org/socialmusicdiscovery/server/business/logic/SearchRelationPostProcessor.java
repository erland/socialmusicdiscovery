package org.socialmusicdiscovery.server.business.logic;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.api.mediaimport.PostProcessor;
import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.model.search.*;
import org.socialmusicdiscovery.server.business.repository.core.ReleaseRepository;

import javax.persistence.EntityManager;
import java.util.*;

public class SearchRelationPostProcessor implements PostProcessor {
    @Inject
    private EntityManager entityManager;

    @Inject
    private ReleaseRepository releaseRepository;

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
        entityManager.createQuery("DELETE from ReleaseSearchRelation").executeUpdate();
        entityManager.createQuery("DELETE from RecordingSearchRelation").executeUpdate();
        entityManager.createQuery("DELETE from WorkSearchRelation").executeUpdate();
        entityManager.createQuery("DELETE from PersonSearchRelation").executeUpdate();
        entityManager.createQuery("DELETE from ArtistSearchRelation").executeUpdate();
        entityManager.getTransaction().commit();

        entityManager.clear();
        entityManager.getTransaction().begin();
        Collection<Release> releases = releaseRepository.findAllWithRelations(null, Arrays.asList("label"));
        long i=0;
        for (Release release : releases) {
            progressHandler.progress(getId(),release.getName(), i+1, (long) releases.size());
            Set<Contributor> releaseContributors = release.getContributors();
            Set<ReleaseSearchRelation> releaseSearchRelations = new HashSet<ReleaseSearchRelation>();
            List<Track> tracks = release.getTracks();
            Set<Contributor> aggregatedContributors = new HashSet<Contributor>(releaseContributors);
            Set<Recording> aggregatedRecordings = new HashSet<Recording>();
            Set<Work> aggregatedWorks = new HashSet<Work>();
            Set<Recording> handledRecordings = new HashSet<Recording>();
            for (Track track : tracks) {
                Recording recording = track.getRecording();
                handledRecordings.add(recording);
                addRecording(aggregatedContributors, aggregatedRecordings, aggregatedWorks,release, recording, track);
            }

            for (RecordingSession session : release.getRecordingSessions()) {
                for (Recording recording : session.getRecordings()) {
                    // We only need to handle this if we haven't already taken care of this recording
                    if(!handledRecordings.contains(recording)) {
                        addRecording(aggregatedContributors, aggregatedRecordings, aggregatedWorks,release, recording, null);
                    }
                }
            }

            // Add relations for all contributors related to either a recording, work which is part of this release or related to the release itself
            for (Contributor contributor : aggregatedContributors) {
                addContributor(releaseSearchRelations, contributor, release, ReleaseSearchRelation.class);
            }
            for (Work work : aggregatedWorks) {
                releaseSearchRelations.add(new ReleaseSearchRelation(release.getId(),work));
            }
            for (Recording recording : aggregatedRecordings) {
                releaseSearchRelations.add(new ReleaseSearchRelation(release.getId(),recording));
            }
            for (ReleaseSearchRelation relation : releaseSearchRelations) {
                entityManager.persist(relation);
            }

            // Let's do a commit every 20'th release
            i++;
            if(i%10 == 0 || abort) {
                entityManager.getTransaction().commit();
                entityManager.getTransaction().begin();
            }
            if(abort) {
                break;
            }
        }
        entityManager.getTransaction().commit();
        if(abort) {
            progressHandler.aborted(getId());
        }else {
            progressHandler.finished(getId());
        }
    }

    private void addRecording(Set<Contributor> aggregatedContributors, Set<Recording> aggregatedRecordings, Set<Work> aggregatedWorks, Release release, Recording recording, Track track) {
        // Get all contributors for a recording session which this recording is part of
        Set<Contributor> releaseContributors = release.getContributors();
        Set<Contributor> sessionContributors = new HashSet<Contributor>();
        Set<RecordingSession> recordingSessions = new HashSet<RecordingSession>();
        for (RecordingSession session : release.getRecordingSessions()) {
            if(session.getRecordings().contains(recording)) {
                sessionContributors.addAll(session.getContributors());
                recordingSessions.add(session);
            }
        }
        Set<RecordingSearchRelation> recordingSearchRelations = new HashSet<RecordingSearchRelation>();
        Set<Contributor> recordingContributors = recording.getContributors();
        aggregatedContributors.addAll(recordingContributors);
        aggregatedContributors.addAll(sessionContributors);
        aggregatedRecordings.add(recording);
        Work work = recording.getWork();
        Set<Contributor> aggregatedWorkContributors = new HashSet<Contributor>();
        Set<Work> works = new HashSet<Work>();
        while(work != null) {
            works.add(work);
            Set<WorkSearchRelation> workSearchRelations = new HashSet<WorkSearchRelation>();
            Set<Contributor> workContributors = work.getContributors();
            aggregatedWorkContributors.addAll(workContributors);
            aggregatedContributors.addAll(workContributors);
            aggregatedWorks.add(work);

            // Add relations for contributors directly related to this work
            for (Contributor contributor : workContributors) {
                for (Work workOrPart : works) {
                    addContributor(workSearchRelations, contributor, workOrPart, WorkSearchRelation.class);
                }
            }

            // Add relations for contributors related to a recording session which contains this work
            // If there are contributors of the same role defined directly on the recording or work, the contributors on the recording session with this role is ignored
            for (Contributor contributor : sessionContributors) {
                if(!containsRole(workContributors,contributor.getType()) &&
                        !containsRole(recordingContributors, contributor.getType())) {
                    for (Work workOrPart : works) {
                        addContributor(workSearchRelations, contributor, workOrPart, WorkSearchRelation.class);
                    }
                }
            }

            // Add relations for contributors related to a recording which contains this work
            // If there are contributors of the same role defined directly on the work, the contributors on the recording with this role is ignored
            for (Contributor contributor : recordingContributors) {
                if(!containsRole(workContributors,contributor.getType())) {
                    addContributor(workSearchRelations, contributor, work, WorkSearchRelation.class);
                }
            }

            // Add relations for contributors related to a release which contains this work
            // If there are contributors of the same role defined directly on the recording, session or work, the contributors on the release with this role is ignored
            for (Contributor contributor : releaseContributors) {
                if(!containsRole(workContributors,contributor.getType()) &&
                        !containsRole(recordingContributors, contributor.getType()) &&
                        !containsRole(sessionContributors, contributor.getType())) {
                    addContributor(workSearchRelations, contributor, work, WorkSearchRelation.class);
                }
            }
            if(release.getLabel() != null) {
                workSearchRelations.add(new WorkSearchRelation(work.getId(),release.getLabel()));
            }
            workSearchRelations.add(new WorkSearchRelation(work.getId(),recording));
            workSearchRelations.add(new WorkSearchRelation(work.getId(),release));
            if(track != null) {
                workSearchRelations.add(new WorkSearchRelation(work.getId(),track));
            }
            if(release.getLabel()!=null) {
                workSearchRelations.add(new WorkSearchRelation(work.getId(),release.getLabel()));
            }
            for (WorkSearchRelation relation : workSearchRelations) {
                // Need to use merge because a work can be part of multiple releases
                entityManager.merge(relation);
            }
            work = work.getParent();
        }

        // Add relations for contributors directly related to this recording
        for (Contributor contributor : recordingContributors) {
            addContributor(recordingSearchRelations, contributor, recording, RecordingSearchRelation.class);
        }

        // Add relations for contributors related to a recording session which this recording is part of
        // If there are contributors of the same role defined directly on the recording, the contributors on the release with this role is ignored.
        for (Contributor contributor : sessionContributors) {
            if(!containsRole(recordingContributors,contributor.getType())) {
                addContributor(recordingSearchRelations, contributor, recording, RecordingSearchRelation.class);
            }
        }

        // Add relations for contributors related to a release which this recording is part of
        // If there are contributors of the same role defined directly on the recording, the contributors on the release with this role is ignored.
        for (Contributor contributor : releaseContributors) {
            if(!containsRole(recordingContributors,contributor.getType())) {
                addContributor(recordingSearchRelations, contributor, recording, RecordingSearchRelation.class);
            }
        }

        // Add relations for contributors related to the work which this recording represent
        for (Contributor contributor : aggregatedWorkContributors) {
            addContributor(recordingSearchRelations, contributor, recording, RecordingSearchRelation.class);
        }
        if(release.getLabel() != null) {
            recordingSearchRelations.add(new RecordingSearchRelation(recording.getId(),release.getLabel()));
        }
        recordingSearchRelations.add(new RecordingSearchRelation(recording.getId(),release));
        if(track != null) {
            recordingSearchRelations.add(new RecordingSearchRelation(recording.getId(),track));
        }
        if(release.getLabel()!=null) {
            recordingSearchRelations.add(new RecordingSearchRelation(recording.getId(),release.getLabel()));
        }
        for (RecordingSearchRelation relation : recordingSearchRelations) {
            // Need to use merge because a recording can be part of multiple releases
            entityManager.merge(relation);
        }
    }
    private <T extends SearchRelation> void addContributor(Set<T> relations, Contributor contributor, SMDEntity owner, Class<T> relationClass) {
        try {
            T relation = relationClass.newInstance();
            relation.setId(owner.getId());
            relation.setReference(contributor);
            relations.add(relation);

            Artist artist = contributor.getArtist();
            relation = relationClass.newInstance();
            relation.setId(owner.getId());
            relation.setReference(artist);
            relations.add(relation);

            // Need to use merge because an artist can be part of multiple releases
            entityManager.merge(new ArtistSearchRelation(artist.getId(),contributor));
            entityManager.merge(new ArtistSearchRelation(artist.getId(),owner));

            Person person = contributor.getArtist().getPerson();
            if(person!=null) {
                relation = relationClass.newInstance();
                relation.setId(owner.getId());
                relation.setReference(person);
                relations.add(relation);
                // Need to use merge because a person can be part of multiple releases
                entityManager.merge(new PersonSearchRelation(person.getId(),owner));
            }
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Check if the list of contributors contain a contributor of the specified type
     * @param contributors List of contributors to check
     * @param type Type of contributor to check for
     * @return true if the list of contributors contains the specified role
     */
    private boolean containsRole(Collection<Contributor> contributors, String type) {
        for (Contributor contributor : contributors) {
            if(contributor.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }
}
