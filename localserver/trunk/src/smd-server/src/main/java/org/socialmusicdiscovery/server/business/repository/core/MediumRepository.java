package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;
import org.socialmusicdiscovery.server.business.model.core.Medium;

@ImplementedBy(MediumRepositoryImpl.class)
public interface MediumRepository extends SMDEntityRepository<Medium> {
}
