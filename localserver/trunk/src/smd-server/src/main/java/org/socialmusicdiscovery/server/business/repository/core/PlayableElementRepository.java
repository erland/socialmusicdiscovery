package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.core.PlayableElementEntity;
import org.socialmusicdiscovery.server.business.repository.SMDIdentityRepository;

import java.util.Collection;

@ImplementedBy(JPAPlayableElementRepository.class)
public interface PlayableElementRepository extends SMDIdentityRepository<PlayableElementEntity> {
    Collection<PlayableElementEntity> findBySmdID(String smdID);
    Collection<PlayableElementEntity> findBySmdIDWithRelations(String smdID, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
}
