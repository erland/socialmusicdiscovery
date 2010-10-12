package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;
import org.socialmusicdiscovery.server.business.model.core.Medium;

import javax.persistence.EntityManager;

public class MediumRepositoryImpl extends SMDEntityRepositoryImpl<Medium> implements MediumRepository {
    public MediumRepositoryImpl() {}
    @Inject
    public MediumRepositoryImpl(EntityManager em) {super(em);}
}
