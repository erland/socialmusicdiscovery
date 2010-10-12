package org.socialmusicdiscovery.server.business.repository.subjective;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.repository.EntityRepositoryImpl;
import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.subjective.Credit;
import org.socialmusicdiscovery.server.business.model.subjective.CreditPK;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class CreditRepositoryImpl extends EntityRepositoryImpl<CreditPK, Credit> implements CreditRepository {
    public CreditRepositoryImpl() {}
    @Inject
    public CreditRepositoryImpl(EntityManager em) {super(em);}

    public Collection<Credit> findCreditsForReleaseRecordingWork(SMDEntity releaseRecordingWork) {
        Query query = entityManager.createQuery("from Credit where releaseRecordingWorkId=:releaseRecordingWorkId");
        query.setParameter("releaseRecordingWorkId",releaseRecordingWork.getId());
        Collection<Credit> result = query.getResultList();
        return result;
    }
}
