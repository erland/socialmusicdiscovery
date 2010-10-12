package org.socialmusicdiscovery.server.business.repository.classification;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;
import org.socialmusicdiscovery.server.business.model.classification.Classification;

import javax.persistence.EntityManager;

public class ClassificationRepositoryImpl extends SMDEntityRepositoryImpl<Classification> implements ClassificationRepository {
    public ClassificationRepositoryImpl() {}
    @Inject
    public ClassificationRepositoryImpl(EntityManager em) {super(em);}
}
