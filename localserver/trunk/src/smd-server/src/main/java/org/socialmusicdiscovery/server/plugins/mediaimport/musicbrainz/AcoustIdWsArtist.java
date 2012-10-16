package org.socialmusicdiscovery.server.plugins.mediaimport.musicbrainz;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
public class AcoustIdWsArtist {
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	private String id;
	private String name;

}
