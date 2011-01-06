package org.socialmusicdiscovery.rcp.content;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.socialmusicdiscovery.rcp.error.FatalApplicationException;
import org.socialmusicdiscovery.rcp.error.RecoverableApplicationException;
import org.socialmusicdiscovery.rcp.event.AbstractObservable;
import org.socialmusicdiscovery.rcp.injections.ClientConfigModule;
import org.socialmusicdiscovery.rcp.prefs.PreferenceConstants;
import org.socialmusicdiscovery.rcp.prefs.ServerConnection;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import com.google.inject.Exposed;
import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * <p>A connector to the server. Reads and writes data over a JSON API.</p>
 * 
 * <p>Some design notes from Erland:
 *  
 * 1. Implement your own model objects that implements the
 * interfaces provided by the server. You can of course choose to use the Entity
 * classes from the server as a temporary solution if you like to.
 * 
 * 2. If you are using Google Guice, add a ClientConfigModule similar to the one
 * in the Apache Pivot based smd-frontend and add a reference to this in your
 * META-INF/services/com.google.inject.AbstractModule file. If you are not using
 * Google Guice, you need to implement a class similar to
 * ClientConfigModule.JSONProvider somewhere. This class is also where you have
 * the mapping between the server interface classes and your implementation
 * classes to allow the JSON encoding/decoding to work correctly.
 * 
 * The ClientConfigModule.JSONProvider class uses an abstract class
 * (AbstractJSONProvider) provided by the server which makes it possible to only
 * provide and interface/implementation mapping and the abstract class will
 * implement all necessary converters.
 * 
 * 3. If you are using Google Guice, change calls from Client.create() to
 * Client.create(config) and add a member variable like this which will be
 * injected from the ClientConfigModule:
 * 
 * @Inject ClientConfig config;
 * 
 * If you are not using Google Guice, you need to create the
 * ClientConfig object some other way, you will find the relevant
 * initialization code in ClientConfigModule.provideClientConfig in the
 * Apache Pivot based smd-frontend.
 * 
 * 4. The actual Client.create call doesn't have to be changed as I
 * suggested yesterday, thanks to the ClientConfig which contains the
 * conversion/mapping, you can still send and return real
 * objects/interfaces with Client.create and don't have to revert to the
 * two step sequence with String and then using Gson.toJson /
 * Gson.fromJson as I suggested yesterday. The only thing you need to do
 * is to provide the ClientConfig object as parameter to the
 * Client.create call.
 * 
 * 5. Depending on how you implement your
 * ClientConfigModule.JSONProvider (step 2 above), you need to provide @Expose
 * annotations on all attributes you like to send over the JSON
 * interface. If you initialize AbstractJSONProvider with "true", it
 * will only include attributes with @Expose annotations in the JSON
 * encoding. You can look at the *Entity classes for an example. I've
 * added @Expose annotation on all attributes which are available in the
 * new interfaces which the entities implement.
 * 
 * I've probably missed something, but look the changes in the Apache
 * Pivot code or ask question if there is something you are wondering
 * about.
 * 
 * /Erland
 * </p>
 * @author Peer Törngren
 * 
 */
public class DataSource extends AbstractObservable implements ModelObject {
	
	public static final String PROP_IS_CONNECTED = "isConnected"; //$NON-NLS-1$

	@Inject	ClientConfig config = newClientConfig();  // TODO does not inject?
	
	private boolean isConnected = false;
	private final boolean isAutoConnect;

	public class Root<T extends SMDIdentity> extends AbstractObservable implements ModelObject {

		private final String name; // for user presentation
		private final String path; // for querying server
		private final Class<T> distinctQueryType; // for querying server
		private GenericType<Collection<T>> genericCollectionQueryType;

		/**
		 * Private constructor, roots should only be instantiated from this
		 * class (may change if we need to mock roots for testing). Argument
		 * list is a bit long, could probably be shortened with better
		 * understanding of generics.
		 * 
		 * @param nodeName
		 *            human readable, for display in UI listings
		 * @param queryPath
		 *            for server query (see {@link #getPath()})
		 * @param distinctElementQueryType
		 *            for server query (see {@link #find(String)})
		 * @param genericCollectionQueryType
		 *            for server query (see {@link #findAll()})
		 */
		private Root(String nodeName, String queryPath, Class<T> distinctElementQueryType, GenericType<Collection<T>> genericCollectionQueryType) {
			this.name = nodeName;
			this.path = queryPath;
			this.distinctQueryType = distinctElementQueryType;
			this.genericCollectionQueryType = genericCollectionQueryType;
		}

		/**
		 * Get all objects of the type that this root handles.
		 * @return {@link List<T>}, possibly empty
		 */
		final synchronized public List<T> findAll() {
	        return get(genericCollectionQueryType);
		}

		/**
		 * Get a distinct object of the type that this root handles, identified
		 * by supplied id.
		 * 
		 * @param entityID
		 * @return T or <code>null</code>
		 */
		public T find(String entityID) {
			// FIXME - cannot open editor from popup menu since we get multiple
			// instances; each "inflated instance" loads new instances for all
			// attributes. Need some kind of cache? Or maybe the inflate() copy 
			// method can handle this? 
			// See org.socialmusicdiscovery.rcp.content.AbstractObservableEntity.inflate()
			String distinctPath = getPath()+"/"+entityID;
			T result = connect(distinctPath).get(distinctQueryType);
			return (T) result;
		}
		
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
			return distinctQueryType;
		}

	}


	public DataSource(boolean isAutoConnect) {
		this.isAutoConnect = isAutoConnect;
	}

	public List<? extends Root> getRoots() {
		Root[] roots = {
			new Root<Release>("Releases", "/releases", Release.class, new GenericType<Collection<Release>>() {}), 
			new Root<Artist>("Artists", "/artists", Artist.class, new GenericType<Collection<Artist>>() {})
		};
		return Arrays.asList(roots); 
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

	public boolean inflate(ObservableEntity shallowEntity) {
		Root root = resolveRoot(shallowEntity);
		SMDIdentity fullEntity = root.find(shallowEntity.getId());
		try {
			// TODO do not create new instances, us cache to re-use already loaded instances? 
			// See org.socialmusicdiscovery.rcp.content.DataSource.Root.find(String)
			new CopyHelper().mergeInto(shallowEntity, fullEntity, Exposed.class);
		} catch (Exception e) {
			throw new FatalApplicationException("Failed to inflate instance: "+shallowEntity, e);  //$NON-NLS-1$
		}
		return true;
	}

	public static ClientConfig newClientConfig() {
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getClasses().add(ClientConfigModule.JSONProvider.class);
		return clientConfig;
	}
}
