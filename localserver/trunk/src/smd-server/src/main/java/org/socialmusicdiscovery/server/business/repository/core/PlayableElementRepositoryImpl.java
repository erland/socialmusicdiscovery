package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;
import org.socialmusicdiscovery.server.business.model.core.PlayableElement;

import javax.persistence.EntityManager;

public class PlayableElementRepositoryImpl extends SMDEntityRepositoryImpl<PlayableElement> implements PlayableElementRepository {
    public PlayableElementRepositoryImpl() {}
    @Inject
    public PlayableElementRepositoryImpl(EntityManager em) {super(em);}
}
