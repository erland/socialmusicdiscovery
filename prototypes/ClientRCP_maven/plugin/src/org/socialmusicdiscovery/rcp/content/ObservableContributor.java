package org.socialmusicdiscovery.rcp.content;

import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Contributor;

import com.google.gson.annotations.Expose;

public class ObservableContributor extends AbstractObservableEntity<Contributor> implements Contributor {

	public static final String PROP_artist = "artist";
	public static final String PROP_type = "type";
	
	@Expose private Artist artist;
	@Expose private String type;

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Artist getArtist() {
		return artist;
	}

	public void setArtist(Artist artist) {
		firePropertyChange(PROP_artist, this.artist, this.artist = artist);
	}

	public void setType(String type) {
		firePropertyChange(PROP_type, this.type, this.type = type);
	}

}
