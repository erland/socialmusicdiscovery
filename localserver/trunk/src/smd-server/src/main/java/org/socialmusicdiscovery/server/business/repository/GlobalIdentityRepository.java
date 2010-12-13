package org.socialmusicdiscovery.server.business.repository;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.GlobalIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;

@ImplementedBy(JPAGlobalIdentityRepository.class)
public interface GlobalIdentityRepository extends EntityRepository<GlobalIdentityEntity, GlobalIdentityEntity> {
    GlobalIdentityEntity findBySourceAndEntity(String source, SMDIdentity entity);
}
