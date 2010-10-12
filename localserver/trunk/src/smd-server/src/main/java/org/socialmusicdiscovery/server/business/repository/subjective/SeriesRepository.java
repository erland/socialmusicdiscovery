package org.socialmusicdiscovery.server.business.repository.subjective;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;
import org.socialmusicdiscovery.server.business.model.subjective.Series;

@ImplementedBy(SeriesRepositoryImpl.class)
public interface SeriesRepository extends SMDEntityRepository<Series> {
}
