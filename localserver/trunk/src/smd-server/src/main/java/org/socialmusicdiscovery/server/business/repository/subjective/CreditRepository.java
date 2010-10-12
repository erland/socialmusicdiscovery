package org.socialmusicdiscovery.server.business.repository.subjective;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.repository.EntityRepository;
import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.subjective.Credit;
import org.socialmusicdiscovery.server.business.model.subjective.CreditPK;

import java.util.Collection;

@ImplementedBy(CreditRepositoryImpl.class)
public interface CreditRepository extends EntityRepository<CreditPK, Credit> {
    Collection<Credit> findCreditsForReleaseRecordingWork(SMDEntity releaseRecordingWorkId);
}
