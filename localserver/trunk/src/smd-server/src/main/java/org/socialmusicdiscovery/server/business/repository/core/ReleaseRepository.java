package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.core.ReleaseEntity;
import org.socialmusicdiscovery.server.business.repository.SMDIdentityRepository;

import java.util.Collection;

@ImplementedBy(JPAReleaseRepository.class)
public interface ReleaseRepository extends SMDIdentityRepository<ReleaseEntity> {
    Collection<ReleaseEntity> findByName(String name);
    Collection<ReleaseEntity> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<ReleaseEntity> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<ReleaseEntity> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<ReleaseEntity> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
}
