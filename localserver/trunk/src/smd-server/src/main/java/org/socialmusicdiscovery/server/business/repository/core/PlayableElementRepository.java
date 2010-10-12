package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;
import org.socialmusicdiscovery.server.business.model.core.PlayableElement;

@ImplementedBy(PlayableElementRepositoryImpl.class)
public interface PlayableElementRepository extends SMDEntityRepository<PlayableElement> {
}
