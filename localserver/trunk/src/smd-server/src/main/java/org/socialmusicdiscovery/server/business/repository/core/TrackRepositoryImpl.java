package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;
import org.socialmusicdiscovery.server.business.model.core.Track;

import javax.persistence.EntityManager;

public class TrackRepositoryImpl extends SMDEntityRepositoryImpl<Track> implements TrackRepository {
    public TrackRepositoryImpl() {}
    @Inject
    public TrackRepositoryImpl(EntityManager em) {super(em);}
}
