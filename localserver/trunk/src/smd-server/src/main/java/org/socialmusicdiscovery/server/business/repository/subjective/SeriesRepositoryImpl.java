package org.socialmusicdiscovery.server.business.repository.subjective;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;
import org.socialmusicdiscovery.server.business.model.subjective.Series;

import javax.persistence.EntityManager;

public class SeriesRepositoryImpl extends SMDEntityRepositoryImpl<Series> implements SeriesRepository {
    public SeriesRepositoryImpl() {}
    @Inject
    public SeriesRepositoryImpl(EntityManager em) {super(em);}
}
