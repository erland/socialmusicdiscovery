package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;
import org.socialmusicdiscovery.server.business.model.core.Track;

@ImplementedBy(TrackRepositoryImpl.class)
public interface TrackRepository extends SMDEntityRepository<Track> {
}
