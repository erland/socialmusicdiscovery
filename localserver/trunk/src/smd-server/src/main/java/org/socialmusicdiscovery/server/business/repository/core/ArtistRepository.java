package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.core.ArtistEntity;
import org.socialmusicdiscovery.server.business.repository.SMDIdentityRepository;

import java.util.Collection;

@ImplementedBy(JPAArtistRepository.class)
public interface ArtistRepository extends SMDIdentityRepository<ArtistEntity> {
    Collection<ArtistEntity> findByName(String name);
    Collection<ArtistEntity> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<ArtistEntity> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<ArtistEntity> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<ArtistEntity> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<ArtistEntity> findByPersonWithRelations(String personId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
}
