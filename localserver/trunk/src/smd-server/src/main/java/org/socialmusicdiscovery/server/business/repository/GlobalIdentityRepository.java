package org.socialmusicdiscovery.server.business.repository;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.GlobalIdentity;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

@ImplementedBy(GlobalIdentityRepositoryImpl.class)
public interface GlobalIdentityRepository extends EntityRepository<GlobalIdentity, GlobalIdentity> {
    GlobalIdentity findBySourceAndEntity(String source, SMDEntity entity);
}
