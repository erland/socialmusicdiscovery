package org.socialmusicdiscovery.rcp.content;

import java.util.Collection;

import javax.ws.rs.core.MediaType;

import org.socialmusicdiscovery.rcp.error.RecoverableApplicationException;
import org.socialmusicdiscovery.rcp.prefs.PreferenceConstants;
import org.socialmusicdiscovery.rcp.prefs.PreferencePage;
import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Release;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource.Builder;

public class DataSource {

	public abstract class Root<T extends SMDEntity<?>> {

		private final String name;
		private final String path;

		public Root(String nodeName, String path) {
			this.name = nodeName;
			this.path = path;
		}

		// see org.socialmusicdiscovery.frontend.SMDApplicationWindow.searchReleases(String, String, String)
		public abstract Collection<T> findAll();

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return getName();
		}

		protected String getPath() {
			return getServerURI()+path;
		}

		protected Builder root() {
			return connect(getPath());
		}

	}

    private class ReleaseRoot extends Root<Release> {

		public ReleaseRoot() {
			super("Releases", "/releases");
		}

		@Override
		synchronized public Collection<Release> findAll() {
	        return root().get(new GenericType<Collection<Release>>() {});
		}
	}

    private class ArtistRoot extends Root<Artist> {

		public ArtistRoot() {
			super("Artists", "/artists");
		}

		@Override
		synchronized public Collection<Artist> findAll() {
	        GenericType<Collection<Artist>> genericType = new GenericType<Collection<Artist>>() {};
			Collection<Artist> collection = get(genericType);
			return collection;
		}

		protected Collection<Artist> get(GenericType<Collection<Artist>> genericType) {
			try {
				return root().get(genericType);
			} catch (ClientHandlerException e) {
				String msg = "Cannot access server: "+getPath();
				String hint = "Please check configuration settings and make sure the server is running.";
				throw new RecoverableApplicationException(msg, hint, e);
			}
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

	private String getServerURI() {
		String hostName = PreferencePage.getString(PreferenceConstants.P_HOSTNAME);
		String port = PreferencePage.getString(PreferenceConstants.P_PORT);
		return "http://"+hostName+":"+port;
	}

	protected Builder connect(String p) {
		return Client.create().resource(p).accept(MediaType.APPLICATION_JSON);
	}
	

}
