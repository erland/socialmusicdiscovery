package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepository;

import java.util.Collection;

@ImplementedBy(ContributorRepositoryImpl.class)
public interface ContributorRepository extends SMDEntityRepository<Contributor> {
    Collection<Contributor> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
}
