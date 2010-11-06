package org.socialmusicdiscovery.server.plugins.mediaimport.squeezeboxserver;

import java.util.List;

public class TrackData {
    private String url;
    private String smdID;
    private String file;
    private String format;
    private List<TagData> tags;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSmdID() {
        return smdID;
    }

    public void setSmdID(String smdID) {
        this.smdID = smdID;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public List<TagData> getTags() {
        return tags;
    }

    public void setTags(List<TagData> tags) {
        this.tags = tags;
    }
}
