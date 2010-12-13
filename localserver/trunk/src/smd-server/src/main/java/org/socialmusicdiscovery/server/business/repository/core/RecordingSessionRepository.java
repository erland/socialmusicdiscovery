package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.core.RecordingSessionEntity;
import org.socialmusicdiscovery.server.business.repository.SMDIdentityRepository;

import java.util.Collection;

@ImplementedBy(JPARecordingSessionRepository.class)
public interface RecordingSessionRepository extends SMDIdentityRepository<RecordingSessionEntity> {
    Collection<RecordingSessionEntity> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
}
