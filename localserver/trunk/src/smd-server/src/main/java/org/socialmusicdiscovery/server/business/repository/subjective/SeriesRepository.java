package org.socialmusicdiscovery.server.business.repository.subjective;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.subjective.SeriesEntity;
import org.socialmusicdiscovery.server.business.repository.SMDIdentityRepository;

@ImplementedBy(JPASeriesRepository.class)
public interface SeriesRepository extends SMDIdentityRepository<SeriesEntity> {
}
