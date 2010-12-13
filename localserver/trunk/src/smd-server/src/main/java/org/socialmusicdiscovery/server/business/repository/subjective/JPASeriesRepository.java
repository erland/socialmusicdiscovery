package org.socialmusicdiscovery.server.business.repository.subjective;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.subjective.SeriesEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;

public class JPASeriesRepository extends AbstractJPASMDIdentityRepository<SeriesEntity> implements SeriesRepository {
    @Inject
    public JPASeriesRepository(EntityManager em) {super(em);}
}
