package org.socialmusicdiscovery.server.plugins.mediaimport.musicbrainz;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = false)
public class AcoustIdWsMedium {

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getTrackCount() {
		return trackCount;
	}

	public void setTrackCount(Integer trackCount) {
		this.trackCount = trackCount;
	}

	public List<AcoustIdWsTrack> getTracks() {
		return tracks;
	}

	public void setTracks(List<AcoustIdWsTrack> tracks) {
		this.tracks = tracks;
	}

	private String format;
	private Integer position;
	
	@JsonProperty("track_count")
	private Integer trackCount;

    private List<AcoustIdWsTrack> tracks;


}
