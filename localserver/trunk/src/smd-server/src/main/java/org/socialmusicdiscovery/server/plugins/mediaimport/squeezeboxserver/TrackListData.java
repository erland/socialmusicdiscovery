package org.socialmusicdiscovery.server.plugins.mediaimport.squeezeboxserver;

import java.util.List;

public class TrackListData {
    private Long count;
    private Long offset;
    private List<TrackData> tracks;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public List<TrackData> getTracks() {
        return tracks;
    }

    public void setTracks(List<TrackData> tracks) {
        this.tracks = tracks;
    }
}
