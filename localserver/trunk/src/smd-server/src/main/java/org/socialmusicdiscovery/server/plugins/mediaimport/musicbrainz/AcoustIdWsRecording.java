package org.socialmusicdiscovery.server.plugins.mediaimport.musicbrainz;


import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = false)
public class AcoustIdWsRecording {
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public Integer getSources() {
		return sources;
	}
	public void setSources(Integer sources) {
		this.sources = sources;
	}


	private String id;
	private String title;
	private Integer duration;
	private Integer sources;
	
    public List<AcoustIdWsArtist> getArtists() {
		return artists;
	}
	public void setArtists(List<AcoustIdWsArtist> artists) {
		this.artists = artists;
	}



	public List<AcoustIdWsReleaseGroup> getReleasegroups() {
		return releasegroups;
	}
	public void setReleasegroups(List<AcoustIdWsReleaseGroup> releasegroups) {
		this.releasegroups = releasegroups;
	}


	private List<AcoustIdWsArtist> artists;
    private List<AcoustIdWsReleaseGroup> releasegroups;
}
