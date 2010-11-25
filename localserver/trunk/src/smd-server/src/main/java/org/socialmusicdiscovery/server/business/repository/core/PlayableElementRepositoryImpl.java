package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.PlayableElement;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class PlayableElementRepositoryImpl extends SMDEntityRepositoryImpl<PlayableElement> implements PlayableElementRepository {
    @Inject
    public PlayableElementRepositoryImpl(EntityManager em) {super(em);}

    public Collection<PlayableElement> findBySmdID(String smdID) {
        return findBySmdIDWithRelations(smdID, null, null);
    }
    public Collection<PlayableElement> findBySmdIDWithRelations(String smdID, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e",mandatoryRelations,optionalRelations)+" where smdID=:smdID");
        query.setParameter("smdID",smdID);
        return query.getResultList();
    }
}
