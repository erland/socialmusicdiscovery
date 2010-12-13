package org.socialmusicdiscovery.server.business.repository.subjective;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.subjective.CreditEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPAEntityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPACreditRepository extends AbstractJPAEntityRepository<CreditEntity, CreditEntity> implements CreditRepository {
    @Inject
    public JPACreditRepository(EntityManager em) {super(em);}

    public Collection<CreditEntity> findCreditsForReleaseRecordingWork(SMDIdentity releaseRecordingWork) {
        Query query = entityManager.createQuery("from CreditEntity where releaseRecordingWorkId=:releaseRecordingWorkId");
        query.setParameter("releaseRecordingWorkId",releaseRecordingWork.getId());
        Collection<CreditEntity> result = query.getResultList();
        return result;
    }
}
