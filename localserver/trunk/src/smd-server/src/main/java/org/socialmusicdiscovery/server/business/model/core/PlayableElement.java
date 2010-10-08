package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "playable_elements")
public class PlayableElement extends SMDEntity<PlayableElement> {
    private String uri;
    private String format;
    private Integer bitrate;

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
