package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;
import org.socialmusicdiscovery.server.business.model.core.Contributor;

@ImplementedBy(ContributorRepositoryImpl.class)
public interface ContributorRepository extends SMDEntityRepository<Contributor> {
}
