package org.socialmusicdiscovery.rcp.content;

import java.util.Collection;

import javax.ws.rs.core.MediaType;

import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Release;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource.Builder;

public class DataSource {

	private static final String SMDSERVER = "localhost";
	private static final String SMDSERVERPORT = "9998";
	private static final String BASE_URI = "http://"+SMDSERVER+":"+SMDSERVERPORT;
	
	public abstract class Root<T extends SMDEntity<?>> {

		private final String name;
		private final String path;

		public Root(String nodeName, String path) {
			this.name = nodeName;
			this.path = path;
		}

		// see org.socialmusicdiscovery.frontend.SMDApplicationWindow.searchReleases(String, String, String)
		public abstract Collection<T> findAll();

		protected Builder builder() {
			return Client.create().resource(BASE_URI+path).accept(MediaType.APPLICATION_JSON);
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return getName();
		}

	}

    private class ReleaseRoot extends Root<Release> {

		public ReleaseRoot() {
			super("Releases", "/releases");
		}

		@Override
		synchronized public Collection<Release> findAll() {
	        return builder().get(new GenericType<Collection<Release>>() {});
		}
	}

    private class ArtistRoot extends Root<Artist> {

		public ArtistRoot() {
			super("Artists", "/artists");
		}

		@Override
		synchronized public Collection<Artist> findAll() {
	        GenericType<Collection<Artist>> genericType = new GenericType<Collection<Artist>>() {};
			Collection<Artist> collection = builder().get(genericType);
			return collection;
		}
	}

	@SuppressWarnings("rawtypes")
	public Root[] getRoots() {
		Root[] roots = {
				new ReleaseRoot(),
				new ArtistRoot(),
		};
		return roots; 
	}
	

}
