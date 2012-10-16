package org.socialmusicdiscovery.server.plugins.mediaimport.musicbrainz;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
public class AcoustIdWsReleaseGroup {
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
    public String getType() {
		return type;
	}
    public void setType(String type) {
		this.type = type;
	}
	public List<AcoustIdWsArtist> getArtists() {
		return artists;
	}
	public void setArtists(List<AcoustIdWsArtist> artists) {
		this.artists = artists;
	}
	public List<AcoustIdWsRelease> getReleases() {
		return releases;
	}
	public void setReleases(List<AcoustIdWsRelease> releases) {
		this.releases = releases;
	}

	private String id;
	private String title;
	private String type;


	private List<AcoustIdWsArtist> artists;
    private List<AcoustIdWsRelease> releases;


}
