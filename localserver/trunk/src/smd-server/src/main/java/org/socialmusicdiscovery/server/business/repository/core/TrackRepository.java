package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;

import java.util.Collection;

@ImplementedBy(TrackRepositoryImpl.class)
public interface TrackRepository extends SMDEntityRepository<Track> {
    Collection<Track> findByName(String name);

    Collection<Track> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations);

    Collection<Track> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations);

    Collection<Track> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);

    Collection<Track> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);

    Collection<Track> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
}
