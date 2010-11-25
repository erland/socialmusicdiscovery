package org.socialmusicdiscovery.server.business.repository.subjective;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.SMDEntityReference;
import org.socialmusicdiscovery.server.business.model.subjective.Relation;
import org.socialmusicdiscovery.server.business.repository.EntityRepository;

import java.util.Collection;

@ImplementedBy(RelationRepositoryImpl.class)
public interface RelationRepository extends EntityRepository<Relation, Relation> {
    Collection<Relation> findRelationsFrom(SMDEntityReference reference);
    Collection<Relation> findRelationsFrom(SMDEntityReference reference, Class relatedTo);
    Collection<Relation> findRelationsTo(SMDEntityReference reference);
    Collection<Relation> findRelationsTo(SMDEntityReference reference, Class relatedFrom);
}
