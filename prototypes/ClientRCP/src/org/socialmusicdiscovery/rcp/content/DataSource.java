package org.socialmusicdiscovery.rcp.content;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.socialmusicdiscovery.rcp.error.ExtendedErrorDialog;
import org.socialmusicdiscovery.rcp.error.FatalApplicationException;
import org.socialmusicdiscovery.rcp.error.RecoverableApplicationException;
import org.socialmusicdiscovery.rcp.event.AbstractObservable;
import org.socialmusicdiscovery.rcp.injections.ClientConfigModule;
import org.socialmusicdiscovery.rcp.prefs.PreferenceConstants;
import org.socialmusicdiscovery.rcp.prefs.ServerConnection;
import org.socialmusicdiscovery.rcp.util.JobUtil;
import org.socialmusicdiscovery.rcp.util.TextUtil;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
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
 */
public class DataSource extends AbstractObservable implements ModelObject {
	private class MyPersistor implements IRunnableWithProgress {

		private final ObservableEntity[] entities;
		private Shell shell;

		public MyPersistor(Shell shell, ObservableEntity[] entities) {
			this.entities = entities;
			this.shell = shell;
		}

		@Override
		public void run(IProgressMonitor monitor) {
			int size = entities.length;
			monitor.beginTask("Save "+size+" elements", size);
			for (final ObservableEntity e : entities) {
				monitor.subTask(e.getName());
				if (!persistOnProperThread(e)) {
					monitor.setCanceled(true);
				}
				monitor.worked(1);
				if (monitor.isCanceled()) {
					return;
				}
			}
			monitor.done();
		}

		/**
		 * Must store on proper thread, or we will get invalid thread access
		 * when firing {@link PropertyChangeEvent}s for the dirty status. Read
		 * more <a href="http://www.eclipse.org/articles/Article-Concurrency/jobs-api.html">here</a>:<br>
		 * <i>The code inside the operation is run in a separate thread in order
		 * to allow the UI to be responsive. This means that any access to UI
		 * components must be done within a syncExec() or asyncExec() or an
		 * invalid thread access exception will be thrown by SWT.</i>
		 * 
		 * @param entity
		 */
		private boolean persistOnProperThread(final ObservableEntity entity) {
			// create a runnable to do the actual job
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					Root root = resolveRoot(entity);
					try {
						root.persist(entity);
					} catch (Exception e) {
						ExtendedErrorDialog dialog = createErrorDialog(root, entity, e); 
						switch (dialog.open()) {
						case ExtendedErrorDialog.RETRY_BUTTON:
							run(); 
							break;
							
						case ExtendedErrorDialog.IGNORE_BUTTON:
							break;

						case ExtendedErrorDialog.CANCEL_BUTTON:
							throw new RuntimeException(e);  // CAUGHT BELOW

						default:
							throw new IllegalStateException(e);
						}
					}
				}

			};
			
			// kludge: run and catch our own exception to return a result
			try {
				shell.getDisplay().syncExec(runnable);
				return true;
			} catch (RuntimeException e) { // THROWN ABOVE
				return false;
			}
		}

		private ExtendedErrorDialog createErrorDialog(Root root, ObservableEntity entity, Exception e) {
			String type = TextUtil.getText(root.getType());
			String task = "Save "+type;
			String problem = "Could not save changed entity:\n"+entity.getName();
			String reason = "Server reported an error ("+getServerURI()+")";
//			RuntimeException e1 = new RuntimeException("Could not save id '"+entity.getId()+"'", e);
			ExtendedErrorDialog dialog = new ExtendedErrorDialog(shell, task, problem, reason, e);
			return dialog;
		}

	}

	public static final String PROP_IS_CONNECTED = "isConnected"; //$NON-NLS-1$

	@Inject	ClientConfig config = newClientConfig();  // TODO does not inject?
	
	private boolean isConnected = false;
	private final boolean isAutoConnect;

	private final DataCache cache;

	private List<Root<? extends SMDIdentity>> roots;

	/**
	 * @author Peer Törngren
	 *
	 * @param <T> the common entity interface that this instance operates on
	 * @param <U> the client-side observable type that this instance returns on queries
	 */
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
		 *            for server query (see {@link #findAll(String)})
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
		final public synchronized <O extends AbstractObservableEntity<T>> List<O> findAll() {
	        List<O> result = new ArrayList<O>();
			for (T serverObject : get(genericCollectionQueryType)) {
				O clientObject = getOrStore(serverObject);
				result.add(clientObject);
			}
			return result;
		}

		final public synchronized <O extends AbstractObservableEntity<T>> Collection<O> findAll(SMDIdentity entity) {
			WebResource resource = Client.create(config).resource(getQueryPath(entity));
			Collection<T> collection = resource.accept(MediaType.APPLICATION_JSON).get(genericCollectionQueryType);

			List<O> result = new ArrayList<O>();
			for (T serverObject : collection) {
				O clientObject = getOrStore(serverObject);
				result.add(clientObject);
			}
			return result;
		}
		// TODO fix generics, eliminate warning
		@SuppressWarnings("unchecked")
		private <O extends AbstractObservableEntity<T>> O getOrStore(T serverObject) {
			return (O) cache.getOrStore(serverObject);
		}

		public IObservableList getObservableChildren() {
			// TODO - should maintain a dynamic list - not return a static list
			return Observables.staticObservableList(findAll());
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return getPath();
		}

		/**
		 * Get path for this root (all instances of this type share this path).
		 * 
		 * @return String
		 * @see #getType()
		 */
		protected String getPath() {
			return getServerURI()+path;
		}

		/**
		 * Get path for a specific instance persisted under/by this root.
		 * @param id
		 * @return String
		 * @see #getPath()
		 */
		protected String getInstancePath(String id) {
			return getPath()+"/"+id; //$NON-NLS-1$
		}

		/**
		 * Get path for querying instances with relations to a specific instance.
		 * @param entity
		 * @see #getPath()
		 */
		private String getQueryPath(SMDIdentity entity) {
			// does this work? Probably not for long .. 
			String queryType = resolveRoot(entity).getType().getSimpleName().toLowerCase(); // e.g. "release" 
			String result = getPath() + "?" + queryType + "=" + entity.getId(); //$NON-NLS-1$ //$NON-NLS-2$
			return result;
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
				Collection<T> collection = Client.create(config).resource(getPath()).accept(MediaType.APPLICATION_JSON).get(genericType);
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

		/**
		 * Update persistent store; Create, Update or Delete supplied entity
		 * depending on its lifecycle state. 
		 * 
		 * @param entity
		 */
		private void persist(ObservableEntity entity) {
			if (isNew(entity)) {
				create(entity);
			} else if (isDeleted(entity)) {
				delete(entity);
			} else {
				update(entity);
			}
		}

		/**
		 * CRUD: <b>C</b>reate
		 * @param entity
		 */
		private void create(ObservableEntity entity) {
			T echo = resource(getPath()).type(MediaType.APPLICATION_JSON).post(getType(), entity);
			entity.setId(echo.getId());
			entity.setDirty(false);
			cache.add(entity);
			
			assert !entity.isDirty() : "Still dirty after save: "+entity;
			assert !isNew(entity) : "Still new after save: "+entity;
			assert echo.getId().equals(entity.getId()) : "Bad post, id corrupted: "+entity+"!="+echo.getId();
			assert cache.contains(entity) : "Cache not updated - entity not found: "+entity;
		}

		/**
		 * CRUD: <b>R</b>ead
		 * @param id
		 * @return T
		 */
		private T read(String id) {
			T result = resource(getInstancePath(id)).accept(MediaType.APPLICATION_JSON).get(distinctQueryType);
			return (T) result;
		}
		
		/**
		 * CRUD: <b>U</b>pdate
		 * @param entity
		 */
		private void update(ObservableEntity entity) {
			assert entity.getId()!=null && entity.getId().trim().length()>0 : "No ID: "+entity;
			assert entity.isDirty() : "Attempt to save unchanged entity: "+entity;
			assert cache.contains(entity) : "Updating uncached entity - should have been inflated and cached before editing: "+entity;
			
			String entityPath = getInstancePath(entity.getId());
			resource(entityPath).type(MediaType.APPLICATION_JSON).put(getType(), entity);
			entity.setDirty(false);
			
			assert !entity.isDirty() : "Still dirty after save: "+entity;
		}

		/**
		 * CRUD: <b>D</b>elete
		 * @param entity
		 */
		private void delete(ObservableEntity entity) {
			assert entity.getId()!=null && entity.getId().trim().length()>0 : "No ID: "+entity;
			assert cache.contains(entity) : "Deleting uncached entity - should have been cached when read: "+entity;
			
			String entityPath = getInstancePath(entity.getId());
			resource(entityPath).type(MediaType.APPLICATION_JSON).delete();
			cache.delete(entity);
			
			assert !cache.contains(entity) : "Cache not updated - entity still present: "+entity;
		}

		private boolean isDeleted(ObservableEntity entity) {
			return false; // FIXME implement delete status! How
		}

		private boolean isNew(ObservableEntity entity) {
			return entity.getId()==null;
		}

	}

	public DataSource(boolean isAutoConnect) {
		this.isAutoConnect = isAutoConnect;
		this.cache = new DataCache();
	}

	@SuppressWarnings("unchecked")
	public List<? extends Root> getRoots() {
		if (roots == null) {
			roots = Arrays.asList(
				new Root<Release>("Releases", "/releases", Release.class, new GenericType<Collection<Release>>() {} ), 
				new Root<Artist>("Artists", "/artists", Artist.class, new GenericType<Collection<Artist>>() {} ), 
				new Root<Track>("Tracks", "/tracks", Track.class, new GenericType<Collection<Track>>() {} )
			);
		}
		return roots;
	}

	/**
	 * Find the {@link Root} for the supplied entity. Fail if no matching root is found. 
	 * @param entity
	 * @return Root
	 */
	@SuppressWarnings("unchecked")
	public <T extends SMDIdentity> Root<T> resolveRoot(T entity) {
		// if number of roots grow, we should probably use a Map<Class, Root> 
		for (Root root : getRoots()) {
			if (root.getType().isInstance(entity)) {
				return root;
			}
		}
		throw new IllegalArgumentException("Cannot resolve root for instance: "+entity);
	}

	/**
	 * Find the {@link Root}s for the supplied types. 
	 * 
	 * @param requestedTypes
	 * @return List<Root>, possibly empty
	 */
	private List resolveRoots(Class<? extends SMDIdentity>... requestedTypes) {
		Set<Root> matches = new HashSet<Root>();
		for (Root prospect : getRoots()) {
			for (Class requestedType : requestedTypes) {
				if (prospect.getType().equals(requestedType)) {
					matches.add(prospect);
				}
			}
		}
		return new ArrayList<Root>(matches);
	}

	/**
	 * Find the {@link Root} for the supplied entity type. Fail if no matching
	 * root is found.
	 * 
	 * @param entity
	 * @return Root
	 */
	@SuppressWarnings("unchecked")
	public <T extends SMDIdentity> Root<T> resolveRoot(Class<T> type) {
		for (Root root : getRoots()) {
			if (root.getType().isAssignableFrom(type)) {
				return root;
			}
		}
		throw new IllegalArgumentException("Cannot resolve root for type: "+type);
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
	
	public boolean isConnected() {
		return isConnected;
	}

	private WebResource resource(String path) {
		return Client.create(config).resource(path);
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
	 * The collection of roots is static - at least for the foreseeable future.
	 * We may want to dynamically add repositories and thus discover new roots. 
	 * If/when that happens, this code must be changed.  
	 */
	@SuppressWarnings("unchecked")
	@Override
	public IObservableList getObservableChildren() {
		return Observables.staticObservableList(resolveRoots(Artist.class, Release.class));
	}

	@Override
	public Object getAdapter(Class adapter) {
		return adapter.isInstance(this) ? this : null;
	}

	/**
	 * Save changes.
	 * @param shell
	 * @param entities
	 * @return <code>true</code> if saved OK, <code>false</code> if not
	 */
	public boolean persist(Shell shell, ObservableEntity... entities) {
		assert entities.length>0 : "Must have at least one entity";
		if (entities.length==1) {
			return new MyPersistor(shell, entities).persistOnProperThread(entities[0]);
		} else {
			return JobUtil.run(shell, new MyPersistor(shell, entities), "Save "+entities.length+" object(s)");
		}
	}

	public <T extends SMDIdentity> boolean inflate(ObservableEntity<T> shallowEntity) {
		Root root = resolveRoot(shallowEntity);
		@SuppressWarnings("unchecked")
		T richEntity = (T) root.read(shallowEntity.getId());
		try {
			cache.merge(richEntity, shallowEntity);
//			copyHelper.mergeInto(unInflated, inflated, Exposed.class);
//			PropertyUtils.copyProperties(unInflated, inflated);
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
