package org.socialmusicdiscovery.server.business.repository;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;

@ImplementedBy(JPASMDIdentityReferenceRepository.class)
public interface SMDIdentityReferenceRepository extends EntityRepository<String, SMDIdentityReferenceEntity> {
}
