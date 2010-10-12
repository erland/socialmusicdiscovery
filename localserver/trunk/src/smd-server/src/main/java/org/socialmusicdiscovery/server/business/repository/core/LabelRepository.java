package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;
import org.socialmusicdiscovery.server.business.model.core.Label;

import java.util.Collection;

@ImplementedBy(LabelRepositoryImpl.class)
public interface LabelRepository extends SMDEntityRepository<Label> {
    public Collection<Label> findByName(String name);
}
