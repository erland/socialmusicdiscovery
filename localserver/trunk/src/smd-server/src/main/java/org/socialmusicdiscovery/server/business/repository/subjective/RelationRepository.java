package org.socialmusicdiscovery.server.business.repository.subjective;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReference;
import org.socialmusicdiscovery.server.business.model.subjective.RelationEntity;
import org.socialmusicdiscovery.server.business.repository.EntityRepository;

import java.util.Collection;

@ImplementedBy(JPARelationRepository.class)
public interface RelationRepository extends EntityRepository<RelationEntity, RelationEntity> {
    Collection<RelationEntity> findRelationsFrom(SMDIdentityReference reference);
    Collection<RelationEntity> findRelationsFrom(SMDIdentityReference reference, Class relatedTo);
    Collection<RelationEntity> findRelationsTo(SMDIdentityReference reference);
    Collection<RelationEntity> findRelationsTo(SMDIdentityReference reference, Class relatedFrom);
}
