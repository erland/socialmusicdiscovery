package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface Release extends SMDIdentity {
    Date getDate();

    void setDate(Date date);

    String getName();

    void setName(String name);

    Label getLabel();

    void setLabel(Label label);

    List<Medium> getMediums();

    void setMediums(List<Medium> mediums);

    List<Track> getTracks();

    void setTracks(List<Track> tracks);

    Set<RecordingSession> getRecordingSessions();

    void setRecordingSessions(Set<RecordingSession> recordingSessions);

    Set<Contributor> getContributors();

    void setContributors(Set<Contributor> contributors);
}
