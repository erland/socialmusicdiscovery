package org.socialmusicdiscovery.rcp.content;

import java.beans.PropertyChangeEvent;
import java.util.Collection;

import javax.ws.rs.core.MediaType;

import org.socialmusicdiscovery.rcp.error.RecoverableApplicationException;
import org.socialmusicdiscovery.rcp.event.ObservableImpl;
import org.socialmusicdiscovery.rcp.prefs.PreferenceConstants;
import org.socialmusicdiscovery.rcp.prefs.PreferencePage;
import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Release;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource.Builder;

public class DataSource extends ObservableImpl {
	
	public static final String PROP_IS_CONNECTED = "isConnected";
	
	private boolean isConnected = false;
	private final boolean isAutoConnect;

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

		/**
		 * If connected or set to autoconnect, check number of children (would 
		 * really want a server-supported call. e.g. based on a DB "count"?).
		 * Otherwise we return <code>true</code>; 
		 * @return boolean
		 */
		public boolean hasChildren() {
			return isConnected || isAutoConnect ? !findAll().isEmpty() : true;
		}

		protected Collection<T> get(GenericType<Collection<T>> genericType) {
			try {
				Collection<T> collection = root().get(genericType);
				isConnected = true;
				return collection;
			} catch (ClientHandlerException e) {
				String msg = "Cannot access server: "+getPath();
				String hint = "Please check configuration settings and make sure the server is running.";
				throw new RecoverableApplicationException(msg, hint, e);
			}
		}

	}

    private class ReleaseRoot extends Root<Release> {

		public ReleaseRoot() {
			super("Releases", "/releases");
		}

		@Override
		synchronized public Collection<Release> findAll() {
	        return get(new GenericType<Collection<Release>>() {});
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

	}

	public DataSource(boolean isAutoConnect) {
		this.isAutoConnect = isAutoConnect;
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

	public boolean isConnected() {
		return isConnected;
	}

	private void setConnected(boolean isConnected) {
		firePropertyChange(PROP_IS_CONNECTED, this.isConnected, this.isConnected = isConnected);
	}

	public void reset() {
		this.setConnected(false);
		// fire "refresh event" to make sure listeners get notified even if we already were disconnected 
		firePropertyChange(new PropertyChangeEvent(this, PROP_IS_CONNECTED, null, null));
	}

}
