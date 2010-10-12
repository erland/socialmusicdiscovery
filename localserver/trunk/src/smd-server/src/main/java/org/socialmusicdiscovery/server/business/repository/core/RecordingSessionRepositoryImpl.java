package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;
import org.socialmusicdiscovery.server.business.model.core.RecordingSession;

import javax.persistence.EntityManager;

public class RecordingSessionRepositoryImpl extends SMDEntityRepositoryImpl<RecordingSession> implements RecordingSessionRepository {
    public RecordingSessionRepositoryImpl() {}
    @Inject
    public RecordingSessionRepositoryImpl(EntityManager em) {super(em);}
}
