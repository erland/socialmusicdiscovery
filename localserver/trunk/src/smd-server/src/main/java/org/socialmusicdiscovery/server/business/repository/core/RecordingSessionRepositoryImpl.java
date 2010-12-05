package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.socialmusicdiscovery.server.business.model.core.RecordingSession;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class RecordingSessionRepositoryImpl extends SMDEntityRepositoryImpl<RecordingSession> implements RecordingSessionRepository {
    ContributorRepository contributorRepository;
    RecordingRepository recordingRepository;

    @Inject
    public RecordingSessionRepositoryImpl(EntityManager em, ContributorRepository contributorRepository, RecordingRepository recordingRepository) {
        super(em);
        this.contributorRepository = contributorRepository;
        this.recordingRepository = recordingRepository;
    }

    public Collection<RecordingSession> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        StringBuffer queryString = new StringBuffer(200);
        String distinct = "distinct ";
        queryString.append("select ").append(distinct).append("e").append(" from ").append("Release as r JOIN r.recordingSessions as e");
        if(mandatoryRelations != null) {
            for (String relation : mandatoryRelations) {
                queryString.append(" JOIN FETCH ").append("e").append(".").append(relation);
            }
        }
        if(optionalRelations != null) {
            for (String relation : optionalRelations) {
                queryString.append(" LEFT JOIN FETCH ").append("e").append(".").append(relation);
            }
        }

        Query query = entityManager.createQuery(queryString.toString()+" WHERE r.id=:release");
        query.setParameter("release", releaseId);
        return query.getResultList();
    }

    @Override
    public void remove(RecordingSession entity) {
        for (Contributor contributor : entity.getContributors()) {
            contributorRepository.remove(contributor);
        }
        for (Recording recording : entity.getRecordings()) {
            recordingRepository.remove(recording);
        }
        super.remove(entity);
    }
}
