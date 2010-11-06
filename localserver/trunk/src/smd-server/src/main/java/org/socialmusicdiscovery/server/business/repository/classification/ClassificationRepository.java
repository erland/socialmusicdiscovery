package org.socialmusicdiscovery.server.business.repository.classification;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;
import org.socialmusicdiscovery.server.business.model.classification.Classification;

import java.util.Collection;

@ImplementedBy(ClassificationRepositoryImpl.class)
public interface ClassificationRepository extends SMDEntityRepository<Classification> {
    Collection<Classification> findByNameAndType(String name, String type);
}
