package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.core.Label;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;

import java.util.Collection;

@ImplementedBy(LabelRepositoryImpl.class)
public interface LabelRepository extends SMDEntityRepository<Label> {
    Collection<Label> findByName(String name);
    Collection<Label> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<Label> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
}
