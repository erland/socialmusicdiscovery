package org.socialmusicdiscovery.server.business.repository;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.SMDEntityReference;

@ImplementedBy(SMDEntityReferenceRepositoryImpl.class)
public interface SMDEntityReferenceRepository extends EntityRepository<String, SMDEntityReference> {
}
