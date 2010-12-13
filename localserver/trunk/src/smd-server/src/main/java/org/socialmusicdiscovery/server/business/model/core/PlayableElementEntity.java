package org.socialmusicdiscovery.server.business.model.core;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;

import javax.persistence.Column;
import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "playable_elements")
@SMDIdentityReferenceEntity.ReferenceType(type = PlayableElement.class)
public class PlayableElementEntity extends AbstractSMDIdentityEntity implements PlayableElement {
    @Column(nullable = false, length = 1024)
    @Expose
    private String uri;
    @Column(nullable = false)
    @Expose
    private String smdID;
    @Column(nullable = false)
    @Expose
    private String format;
    @Expose
    private Integer bitrate;

    public String getSmdID() {
        return smdID;
    }

    public void setSmdID(String smdID) {
        this.smdID = smdID;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getBitrate() {
        return bitrate;
    }

    public void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }
}
