package org.socialmusicdiscovery.server.business.model.subjective;

import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.SMDEntityReference;
import org.socialmusicdiscovery.server.business.model.core.Person;

import javax.persistence.*;

@javax.persistence.Entity
@Table(name="credits")
@IdClass(CreditPK.class)
public class Credit {
    @Id
    private String type;

    @Id
    private String artistPersonId;

    @Id
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
