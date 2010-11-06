package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.core.Work;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;

import java.util.Collection;

@ImplementedBy(WorkRepositoryImpl.class)
public interface WorkRepository extends SMDEntityRepository<Work> {
    Collection<Work> findByName(String name);
    Collection<Work> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
}
