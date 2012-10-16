package org.socialmusicdiscovery.server.plugins.mediaimport.musicbrainz;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.socialmusicdiscovery.server.plugins.mediaimport.TrackData;

@JsonIgnoreProperties
public class AcoustIdWsResult {
	public String getId() {
		return id;
	}
    public void setId(String id) {
		this.id = id;
	}
	public Float getScore() {
		return score;
	}
	public void setScore(Float score) {
		this.score = score;
	}
	public List<AcoustIdWsRecording> getRecordings() {
		return recordings;
	}
	public void setRecordings(List<AcoustIdWsRecording> recordings) {
		this.recordings = recordings;
	}
	private String id;
    private Float score;
    private List<AcoustIdWsRecording> recordings;
}
