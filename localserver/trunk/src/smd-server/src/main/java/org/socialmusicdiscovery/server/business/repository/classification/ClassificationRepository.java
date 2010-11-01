package org.socialmusicdiscovery.server.business.repository.classification;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;
import org.socialmusicdiscovery.server.business.model.classification.Classification;

@ImplementedBy(ClassificationRepositoryImpl.class)
public interface ClassificationRepository extends SMDEntityRepository<Classification> {
}