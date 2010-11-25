package org.socialmusicdiscovery.server.business.repository;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.SMDEntityReference;

import javax.persistence.EntityManager;

public class SMDEntityReferenceRepositoryImpl extends EntityRepositoryImpl<String, SMDEntityReference> implements SMDEntityReferenceRepository {
    @Inject
    public SMDEntityReferenceRepositoryImpl(EntityManager em) {super(em);}
}
