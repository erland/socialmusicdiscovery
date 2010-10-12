package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;
import org.socialmusicdiscovery.server.business.model.core.Recording;

import java.util.Collection;

@ImplementedBy(RecordingRepositoryImpl.class)public interface RecordingRepository extends SMDEntityRepository<Recording> {
    public Collection<Recording> findByName(String name);
}
