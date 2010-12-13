package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.MediumEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;

public class JPAMediumRepository extends AbstractJPASMDIdentityRepository<MediumEntity> implements MediumRepository {
    @Inject
    public JPAMediumRepository(EntityManager em) {super(em);}
}
