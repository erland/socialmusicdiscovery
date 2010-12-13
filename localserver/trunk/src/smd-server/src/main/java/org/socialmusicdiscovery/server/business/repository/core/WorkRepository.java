package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.core.WorkEntity;
import org.socialmusicdiscovery.server.business.repository.SMDIdentityRepository;

import java.util.Collection;

@ImplementedBy(JPAWorkRepository.class)
public interface WorkRepository extends SMDIdentityRepository<WorkEntity> {
    Collection<WorkEntity> findByName(String name);
    Collection<WorkEntity> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<WorkEntity> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<WorkEntity> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<WorkEntity> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
}
