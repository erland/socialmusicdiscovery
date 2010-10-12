package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;
import org.socialmusicdiscovery.server.business.model.core.Work;

import java.util.Collection;

@ImplementedBy(WorkRepositoryImpl.class)
public interface WorkRepository extends SMDEntityRepository<Work> {
    public Collection<Work> findByName(String name);
}
