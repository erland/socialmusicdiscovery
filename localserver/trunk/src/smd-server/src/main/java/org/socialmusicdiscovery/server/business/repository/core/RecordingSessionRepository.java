package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;
import org.socialmusicdiscovery.server.business.model.core.RecordingSession;

@ImplementedBy(RecordingSessionRepositoryImpl.class)
public interface RecordingSessionRepository extends SMDEntityRepository<RecordingSession> {
}
