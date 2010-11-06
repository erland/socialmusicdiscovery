package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;

import java.util.Collection;

@ImplementedBy(RecordingRepositoryImpl.class)public interface RecordingRepository extends SMDEntityRepository<Recording> {
    Collection<Recording> findByName(String name);
    Collection<Recording> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
}
