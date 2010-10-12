package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;
import org.socialmusicdiscovery.server.business.model.core.Artist;

import java.util.Collection;

@ImplementedBy(ArtistRepositoryImpl.class)
public interface ArtistRepository extends SMDEntityRepository<Artist> {
    Collection<Artist> findByName(String name);
}
