package org.socialmusicdiscovery.server.business.repository;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;

import javax.persistence.EntityManager;

public class JPASMDIdentityReferenceRepository extends AbstractJPAEntityRepository<String, SMDIdentityReferenceEntity> implements SMDIdentityReferenceRepository {
    @Inject
    public JPASMDIdentityReferenceRepository(EntityManager em) {super(em);}
}
