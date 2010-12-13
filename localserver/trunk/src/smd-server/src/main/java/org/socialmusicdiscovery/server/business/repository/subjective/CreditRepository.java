package org.socialmusicdiscovery.server.business.repository.subjective;

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.subjective.CreditEntity;
import org.socialmusicdiscovery.server.business.repository.EntityRepository;

import java.util.Collection;

@ImplementedBy(JPACreditRepository.class)
public interface CreditRepository extends EntityRepository<CreditEntity, CreditEntity> {
    Collection<CreditEntity> findCreditsForReleaseRecordingWork(SMDIdentity releaseRecordingWorkId);
}
