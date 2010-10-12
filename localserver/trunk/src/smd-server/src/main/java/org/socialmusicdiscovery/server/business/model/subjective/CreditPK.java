package org.socialmusicdiscovery.server.business.model.subjective;

import javax.persistence.Basic;
import javax.persistence.Column;
import java.io.Serializable;

public class CreditPK implements Serializable {
    private String type;
    @Column(name="artist_person_id", length = 36)
    private String artistPersonId;
    @Column(name="release_recording_work_id", length = 36)
    private String releaseRecordingWorkId;

    public CreditPK() {}
    public CreditPK(String type, String artistPersonId, String releaseRecordingWorkId) {
        this.type = type;
        this.artistPersonId = artistPersonId;
        this.releaseRecordingWorkId = releaseRecordingWorkId;
    }
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
    public boolean equals(Object object) {
        if (object instanceof CreditPK) {
            CreditPK pk = (CreditPK) object;
            return artistPersonId.equals(pk.getArtistPersonId()) && releaseRecordingWorkId.equals(pk.getReleaseRecordingWorkId()) && type.equals(pk.getType());
        } else {
            return false;
        }
    }

    public int hashCode() {
        int hashCode = (artistPersonId == null ? 0 : artistPersonId.hashCode());
        hashCode = 31 * hashCode + (releaseRecordingWorkId == null ? 0 : releaseRecordingWorkId.hashCode());
        hashCode = 31 * hashCode + (type == null ? 0 : type.hashCode());
        return hashCode;
    }
}
