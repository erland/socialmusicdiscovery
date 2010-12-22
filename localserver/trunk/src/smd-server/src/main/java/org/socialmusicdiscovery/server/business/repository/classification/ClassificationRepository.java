package org.socialmusicdiscovery.server.business.repository.classification;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.repository.SMDIdentityRepository;

import java.util.Collection;

@ImplementedBy(JPAClassificationRepository.class)
public interface ClassificationRepository extends SMDIdentityRepository<ClassificationEntity> {
    Collection<ClassificationEntity> findByNameAndType(String name, String type);
    Collection<ClassificationEntity> findByReference(String reference);
    Collection<ClassificationEntity> findByTypeAndReference(String type, String reference);
}
