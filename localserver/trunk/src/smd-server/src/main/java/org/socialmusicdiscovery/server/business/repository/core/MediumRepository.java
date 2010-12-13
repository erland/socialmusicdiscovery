package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.core.MediumEntity;
import org.socialmusicdiscovery.server.business.repository.SMDIdentityRepository;

@ImplementedBy(JPAMediumRepository.class)
public interface MediumRepository extends SMDIdentityRepository<MediumEntity> {
}
