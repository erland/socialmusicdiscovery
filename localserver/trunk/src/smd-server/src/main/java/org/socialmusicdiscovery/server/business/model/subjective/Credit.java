package org.socialmusicdiscovery.server.business.model.subjective;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@javax.persistence.Entity
@Table(name = "credits")
@IdClass(Credit.class)
public class Credit implements Serializable {
    @Id
    private String type;

    @Id
    @Column(name = "artist_person_id", length = 36)
    private String artistPersonId;

    @Id
    @Column(name = "release_recording_work_id", length = 36)
    private String releaseRecordingWorkId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArtistPersonId() {
        return artistPersonId;
    }

    public void setArtistPersonId(String artistPersonId) {
        this.artistPersonId = artistPersonId;
    }

    public String getReleaseRecordingWorkId() {
        return releaseRecordingWorkId;
    }

    public void setReleaseRecordingWorkId(String releaseRecordingWorkId) {
        this.releaseRecordingWorkId = releaseRecordingWorkId;
    }
}
