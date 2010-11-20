package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "playable_element")
@javax.persistence.Entity
@Table(name = "playable_elements")
public class PlayableElement extends SMDEntity<PlayableElement> {
    @Column(nullable = false, length = 1024)
    private String uri;
    @Column(nullable = false)
    private String smdID;
    @Column(nullable = false)
    private String format;
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
