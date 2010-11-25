package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.socialmusicdiscovery.server.business.model.core.RecordingSession;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;

import javax.persistence.EntityManager;

public class RecordingSessionRepositoryImpl extends SMDEntityRepositoryImpl<RecordingSession> implements RecordingSessionRepository {
    ContributorRepository contributorRepository;
    RecordingRepository recordingRepository;

    @Inject
    public RecordingSessionRepositoryImpl(EntityManager em, ContributorRepository contributorRepository, RecordingRepository recordingRepository) {
        super(em);
        this.contributorRepository = contributorRepository;
        this.recordingRepository = recordingRepository;
    }

    @Override
    public void remove(RecordingSession entity) {
        for (Contributor contributor : entity.getContributors()) {
            contributorRepository.remove(contributor);
        }
        for (Recording recording : entity.getRecordings()) {
            recordingRepository.remove(recording);
        }
        super.remove(entity);
    }
}
