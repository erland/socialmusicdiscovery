package org.socialmusicdiscovery.server.plugins.mediaimport.musicbrainz;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = false)
public class AcoustIdWsRelease {
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}


	private String id;

	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public Integer getTrackCount() {
		return trackCount;
	}
	public void setTrackCount(Integer trackCount) {
		this.trackCount = trackCount;
	}
	
	public Integer getMedium_count() {
		return mediumCount;
	}
	public void setMediumCount(Integer mediumCount) {
		this.mediumCount = mediumCount;
	}
	public List<AcoustIdWsMedium> getMediums() {
		return mediums;
	}
	public void setMediums(List<AcoustIdWsMedium> mediums) {
		this.mediums = mediums;
	}
	public AcoustIdWsDate getDate() {
		return date;
	}
	public void setDate(AcoustIdWsDate date) {
		this.date = date;
	}


	private String country;
	private String title;
	
    public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	@JsonProperty("track_count")
	private Integer trackCount;

	@JsonProperty("medium_count")
    private Integer mediumCount;
    private List<AcoustIdWsMedium> mediums;
    private AcoustIdWsDate date;



}
