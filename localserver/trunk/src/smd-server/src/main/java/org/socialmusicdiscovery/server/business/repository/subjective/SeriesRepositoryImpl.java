package org.socialmusicdiscovery.server.business.repository.subjective;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.subjective.Series;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;

import javax.persistence.EntityManager;

public class SeriesRepositoryImpl extends SMDEntityRepositoryImpl<Series> implements SeriesRepository {
    @Inject
    public SeriesRepositoryImpl(EntityManager em) {super(em);}
}
