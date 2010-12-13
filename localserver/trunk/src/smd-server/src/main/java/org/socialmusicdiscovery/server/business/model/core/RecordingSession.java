package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import java.util.Date;
import java.util.Set;

public interface RecordingSession extends SMDIdentity {
    Date getDate();

    void setDate(Date date);

    Set<Contributor> getContributors();

    void setContributors(Set<Contributor> contributors);

    Set<Recording> getRecordings();

    void setRecordings(Set<Recording> recordings);
}
