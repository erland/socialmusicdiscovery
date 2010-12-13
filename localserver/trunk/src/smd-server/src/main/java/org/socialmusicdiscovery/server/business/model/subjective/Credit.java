package org.socialmusicdiscovery.server.business.model.subjective;

public interface Credit {
    String getType();

    void setType(String type);

    String getArtistPersonId();

    void setArtistPersonId(String artistPersonId);

    String getReleaseRecordingWorkId();

    void setReleaseRecordingWorkId(String releaseRecordingWorkId);
}
