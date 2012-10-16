package org.socialmusicdiscovery.server.plugins.mediaimport.musicbrainz;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
public class AcoustIdWsTrack {
	
    private Integer position;

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

}
