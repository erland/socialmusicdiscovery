package org.socialmusicdiscovery.rcp.content;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.socialmusicdiscovery.rcp.error.RecoverableApplicationException;
import org.socialmusicdiscovery.rcp.event.AbstractObservable;
import org.socialmusicdiscovery.rcp.injections.ClientConfigModule;
import org.socialmusicdiscovery.rcp.prefs.PreferenceConstants;
import org.socialmusicdiscovery.rcp.prefs.ServerConnection;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Release;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class DataSource extends AbstractObservable implements ModelObject {
	
	public static final String PROP_IS_CONNECTED = "isConnected"; //$NON-NLS-1$

	@Inject	ClientConfig config = newClientConfig();  // TODO does not inject?
	
	private boolean isConnected = false;
	private final boolean isAutoConnect;

	public abstract class Root<T extends SMDIdentity> extends AbstractObservable implements ModelObject {

		private final String name; // for user presentation
		private final String path; // for querying server
		private final Class<T> type; // for querying server

		public Root(String nodeName, String path, Class<T> type) {
			this.name = nodeName;
			this.path = path;
			this.type = type;
		}

		/*
		 * 1. Implement your own model objects that implements the interfaces
		 * provided by the server. You can of course choose to use the Entity
		 * classes from the server as a temporary solution if you like to.
		 * 
		 * 2. If you are using Google Guice, add a ClientConfigModule similar to
		 * the one in the Apache Pivot based smd-frontend and add a reference to
		 * this in your META-INF/services/com.google.inject.AbstractModule file.
		 * If you are not using Google Guice, you need to implement a class
		 * similar to ClientConfigModule.JSONProvider somewhere. This class is
		 * also where you have the mapping between the server interface classes
		 * and your implementation classes to allow the JSON encoding/decoding
		 * to work correctly.
		 * 
		 * The ClientConfigModule.JSONProvider class uses an abstract class
		 * (AbstractJSONProvider) provided by the server which makes it possible
		 * to only provide and interface/implementation mapping and the abstract
		 * class will implement all necessary converters.
		 * 
		 * 3. If you are using Google Guice, change calls from Client.create()
		 * to Client.create(config) and add a member variable like this which
		 * will be injected from the ClientConfigModule:
		 * 
		 * @Inject ClientConfig config;
		 * 
		 * If you are not using Google Guice, you need to create the
		 * ClientConfig object some other way, you will find the
		 * relevant initialization code in
		 * ClientConfigModule.provideClientConfig in the Apache Pivot
		 * based smd-frontend.
		 * 
		 * 4. The actual Client.create call doesn't have to be changed
		 * as I suggested yesterday, thanks to the ClientConfig which
		 * contains the conversion/mapping, you can still send and
		 * return real objects/interfaces with Client.create and don't
		 * have to revert to the two step sequence with String and then
		 * using Gson.toJson / Gson.fromJson as I suggested yesterday.
		 * The only thing you need to do is to provide the ClientConfig
		 * object as parameter to the Client.create call.
		 * 
		 * 5. Depending on how you implement your
		 * ClientConfigModule.JSONProvider (step 2 above), you need to
		 * provide @Expose annotations on all attributes you like to
		 * send over the JSON interface. If you initialize
		 * AbstractJSONProvider with "true", it will only include
		 * attributes with @Expose annotations in the JSON encoding. You
		 * can look at the *Entity classes for an example. I've added @Expose
		 * annotation on all attributes which are available in the new
		 * interfaces which the entities implement.
		 * 
		 * I've probably missed something, but look the changes in the
		 * Apache Pivot code or ask question if there is something you
		 * are wondering about.
		 * 
		 * /Erland
		 */
		public abstract List<T> findAll();

		public T find(String id) {
			
//		    Release release = Client.create().resource("http://localhost:9998/releases/" + entity.getId()).accept(MediaType.APPLICATION_JSON).get(Release.class);
			// TODO use cache
			String distinctPath = getPath()+"/"+id;
			T result = connect(distinctPath).get(type);
			return (T) result;
		}
		
		/**
		 * (As a parent, I find the name of this method quite amusing / Peer) 
		 */
		public IObservableList getObservableChildren() {
			List<T> observableEntities = findAll();
			return Observables.staticObservableList(observableEntities);
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return getPath();
		}

		protected String getPath() {
			return getServerURI()+path;
		}

		protected Builder root() {
			return connect(getPath());
		}

		/**
		 * If connected (or set to autoconnect), check number of children (would 
		 * really want a server-supported call. e.g. based on a DB "count"?).
		 * Otherwise we return <code>true</code>; 
		 * @return boolean
		 */
		public boolean hasChildren() {
			return isConnected || isAutoConnect ? !findAll().isEmpty() : true;
		}

		protected List<T> get(GenericType<Collection<T>> genericType) {
			try {
				Collection<T> collection = root().get(genericType);
				isConnected = true;
				return new ArrayList<T>(collection);
			} catch (ClientHandlerException e) {
				String msg = "Cannot access server: "+getPath(); //$NON-NLS-1$
				String hint = "Please check configuration settings and make sure the server is running."; //$NON-NLS-1$
				throw new RecoverableApplicationException(msg, hint, e);
			}
		}

		@Override
		public Object getAdapter(Class adapter) {
			return adapter.isInstance(this) ? this : null;
		}

		public Class<T> getType() {
			return type;
		}

	}

    private class ReleaseRoot extends Root<Release> {

    	public ReleaseRoot() {
			super("Releases", "/releases", Release.class); //$NON-NLS-1$ //$NON-NLS-2$
		}

    	@Override
		synchronized public List<Release> findAll() {
	        return get(new GenericType<Collection<Release>>() {});
		}

    }

    private class ArtistRoot extends Root<Artist> {

		public ArtistRoot() {
			super("Artists", "/artists", Artist.class); //$NON-NLS-1$ //$NON-NLS-2$
		}

		@Override
		synchronized public List<Artist> findAll() {
	        return get(new GenericType<Collection<Artist>>() {});
		}

    }

	public DataSource(boolean isAutoConnect) {
		this.isAutoConnect = isAutoConnect;
	}

	public List<? extends Root> getRoots() {
		return Arrays.asList((Root) new ReleaseRoot(), new ArtistRoot()); 
	}

	public Root resolveRoot(Object entity) {
		for (Root root : getRoots()) {
			if (root.getType().isInstance(entity)) {
				return root;
			}
		}
		throw new IllegalArgumentException("Cannot resolve root for unknown element type: "+entity);
	}
	
	private String getServerURI() {
		String hostName = getServerName();
		String port = getServerPort();
		return "http://"+hostName+":"+port; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static String getServerPort() {
		return ServerConnection.getString(PreferenceConstants.P_PORT);
	}

	private static String getServerName() {
		return ServerConnection.getString(PreferenceConstants.P_HOSTNAME);
	}
	
	protected Builder connect(String p) {
		return Client.create(config).resource(p).accept(MediaType.APPLICATION_JSON);
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

	@Override
	public String getName() {
		return getServerName()+":"+getServerPort(); //$NON-NLS-1$
	}

	/**
	 * The collection of roots does not change.
	 */
	@Override
	public IObservableList getObservableChildren() {
		return Observables.staticObservableList(getRoots());
	}

	@Override
	public Object getAdapter(Class adapter) {
		return adapter.isInstance(this) ? this : null;
	}

	public static ClientConfig newClientConfig() {
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getClasses().add(ClientConfigModule.JSONProvider.class);
		return clientConfig;
	}
}
