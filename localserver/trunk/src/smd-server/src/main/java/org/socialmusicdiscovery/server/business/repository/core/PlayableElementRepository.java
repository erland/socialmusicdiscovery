package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.core.PlayableElement;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;

import java.util.Collection;

@ImplementedBy(PlayableElementRepositoryImpl.class)
public interface PlayableElementRepository extends SMDEntityRepository<PlayableElement> {
    Collection<PlayableElement> findBySmdID(String smdID);
    Collection<PlayableElement> findBySmdIDWithRelations(String smdID, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
}
