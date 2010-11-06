package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;

import java.util.Collection;

@ImplementedBy(ReleaseRepositoryImpl.class)
public interface ReleaseRepository extends SMDEntityRepository<Release> {
    Collection<Release> findByName(String name);
    Collection<Release> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
}
