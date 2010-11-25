package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;

import java.util.Collection;

@ImplementedBy(ArtistRepositoryImpl.class)
public interface ArtistRepository extends SMDEntityRepository<Artist> {
    Collection<Artist> findByName(String name);
    Collection<Artist> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<Artist> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<Artist> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<Artist> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<Artist> findByPersonWithRelations(String personId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
}
