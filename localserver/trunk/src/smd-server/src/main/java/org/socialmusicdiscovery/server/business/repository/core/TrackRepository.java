package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.core.TrackEntity;
import org.socialmusicdiscovery.server.business.repository.SMDIdentityRepository;

import java.util.Collection;

@ImplementedBy(JPATrackRepository.class)
public interface TrackRepository extends SMDIdentityRepository<TrackEntity> {
    Collection<TrackEntity> findByName(String name);

    Collection<TrackEntity> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations);

    Collection<TrackEntity> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations);

    Collection<TrackEntity> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);

    Collection<TrackEntity> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);

    Collection<TrackEntity> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
}
