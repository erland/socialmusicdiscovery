package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.PlayableElementEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPAPlayableElementRepository extends AbstractJPASMDIdentityRepository<PlayableElementEntity> implements PlayableElementRepository {
    @Inject
    public JPAPlayableElementRepository(EntityManager em) {super(em);}

    public Collection<PlayableElementEntity> findBySmdID(String smdID) {
        return findBySmdIDWithRelations(smdID, null, null);
    }
    public Collection<PlayableElementEntity> findBySmdIDWithRelations(String smdID, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations,optionalRelations)+" where smdID=:smdID");
        query.setParameter("smdID",smdID);
        return query.getResultList();
    }
}
