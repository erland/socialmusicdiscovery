/*
 *  Copyright 2010-2011, Social Music Discovery project
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of Social Music Discovery project nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.socialmusicdiscovery.yggdrasil.datasource.rest;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ProgressMonitor;
import javax.ws.rs.core.MediaType;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.socialmusicdiscovery.server.api.OperationStatus;
import org.socialmusicdiscovery.server.api.management.mediaimport.MediaImportStatus;
import org.socialmusicdiscovery.server.api.management.mediaimport.MediaImportStatus.Status;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Label;
import org.socialmusicdiscovery.server.business.model.core.PlayableElement;
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.server.business.model.core.Work;
import org.socialmusicdiscovery.yggdrasil.foundation.content.AbstractObservableEntity;
import org.socialmusicdiscovery.yggdrasil.foundation.content.DataSource;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ItemFactory;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ModelObject;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableArtist;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableContributor;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableEntity;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableLabel;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservablePlayableElement;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableRecording;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableRelease;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableTrack;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableWork;
import org.socialmusicdiscovery.yggdrasil.foundation.error.ExtendedErrorDialog;
import org.socialmusicdiscovery.yggdrasil.foundation.error.FatalApplicationException;
import org.socialmusicdiscovery.yggdrasil.foundation.error.RecoverableApplicationException;
import org.socialmusicdiscovery.yggdrasil.foundation.event.AbstractObservable;
import org.socialmusicdiscovery.yggdrasil.foundation.prefs.PreferenceConstants;
import org.socialmusicdiscovery.yggdrasil.foundation.prefs.ServerConnection;
import org.socialmusicdiscovery.yggdrasil.foundation.util.JobUtil;
import org.socialmusicdiscovery.yggdrasil.foundation.util.TextUtil;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
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
public class RESTDataSource extends AbstractObservable implements ModelObject, DataSource {

	/**
	 * Loads instances with callback to a {@link ProgressMonitor}.
	 */
	private class MyLoader implements IRunnableWithProgress {

		private final MyRoot[] roots;
		private final Shell shell;

		public MyLoader(MyRoot... visibleRoots) {
			this.roots = visibleRoots;
			this.shell = Display.getCurrent().getActiveShell();
		}

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			if (roots.length==1) {
				loadSingle(monitor);
			} else if (roots.length>1) {
				loadMany(monitor);
			} else {
				// no-op
			}
		}

		private void loadMany(IProgressMonitor monitor) {
			monitor.beginTask("Load content: "+roots.length + " roots.", roots.length);
			for (final MyRoot root : roots) {
				monitor.subTask("Load "+root.name+" ...");
				shell.getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						root.load(); 
					}
				});
				monitor.worked(1);
			}
			monitor.done();
		}
		
		private void loadSingle(IProgressMonitor monitor) {
			final MyRoot root = roots[0];
//			monitor.beginTask("Load root: "+root.getName(), IProgressMonitor.UNKNOWN);  // TODO run in background
			monitor.beginTask("Load root: "+root.getName(), 2);
			monitor.worked(1);
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					root.load(); 
				}
			});
			monitor.done();
		}
	}
	/**
	 * Runs persistency operations on proper thread with callback to a {@link ProgressMonitor}.
	 * Dispatches the actual work to {@link MyInnerPersistor}. 
	 */
	private class MyPersistor implements IRunnableWithProgress {

		private final ObservableEntity[] entities;
		private final Shell shell;

		public MyPersistor(Shell shell, ObservableEntity[] entities) {
			assert shell!=null : "Must have shell!";
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
			// kludge: run and catch our own exception to return a result
			try {
				Runnable runnable = new MyInnerPersistor(shell, entity);
				shell.getDisplay().syncExec(runnable);
				return true;
			} catch (RuntimeException e) { // THROWN BY RUNNABLE
				return false;
			}
		}

	}

	/**
	 * The inner class that implements the actual save operations.
	 * This class is run by {@link MyPersistor}.
	 */
	private final class MyInnerPersistor implements Runnable {
		private final ObservableEntity entity;
		private final Shell shell;
		private final Set<ObservableEntity> toDelete;
		private final Set<ObservableEntity> toSave;
		
		private MyInnerPersistor(Shell shell, ObservableEntity entity) {
			this.shell = shell;
			this.entity = entity;
			this.toDelete = entity.getRemovedDependents();
			this.toSave = entity.getSaveableDependents();
		}
	
		@Override
		public void run() {
			for (ObservableEntity e : toSave) {
				if (e.isDirty()) {
					doSave(e);
				}
			}
			for (ObservableEntity e : toDelete) {
				doDelete(e);
			}
			doSave(entity);
		}

		private void doDelete(ObservableEntity victim) {
			MyRoot root = resolveRoot(victim);
			try {
				root.delete(victim);
			} catch (Exception e) {
				handleError(victim, root, e);
			}
		}

		private void doSave(ObservableEntity toSave) {
			MyRoot root = resolveRoot(toSave);
			try {
				root.persist(toSave);
			} catch (Exception e) {
				handleError(toSave, root, e);
			}
		}

		private void handleError(ObservableEntity toSave, MyRoot root, Exception e) {
			ExtendedErrorDialog dialog = createErrorDialog(root, toSave, e);
			switch (dialog.open()) {
			case ExtendedErrorDialog.RETRY_BUTTON:
				run();
				break;

			case ExtendedErrorDialog.IGNORE_BUTTON:
				break;

			case ExtendedErrorDialog.CANCEL_BUTTON:
				throw new RuntimeException(e); // CAUGHT BY OUTER PERISTOR

			default:
				throw new IllegalStateException(e);
			}
		}

		private ExtendedErrorDialog createErrorDialog(MyRoot root, ObservableEntity entity, Exception e) {
			String type = TextUtil.getText(root.getType());
			String task = "Save "+type;
			String problem = "Could not save changed entity:\n"+entity.getName();
			String reason = "Server reported an error ("+getServerURI()+")";
//			RuntimeException e1 = new RuntimeException("Could not save id '"+entity.getId()+"'", e);
			ExtendedErrorDialog dialog = new ExtendedErrorDialog(shell, task, problem, reason, e);
			return dialog;
		}
	}

	@Inject	ClientConfig config = newClientConfig();  // TODO does not inject?
	
	private boolean isConnected = false;
	private boolean isAutoConnect = true;

	private final DataCache cache = new DataCache();  // holds persistent objects that have been read from server

	private List<MyRoot<? extends SMDIdentity>> roots;

	private boolean isLazyRootLoad = true;  // if true, load roots when expanded (not when connected)

	public class MyRoot<T extends SMDIdentity> extends AbstractObservable implements ModelObject, ItemFactory<T>, DataSource.Root<T> {

		private final class MyEntityCollectionListener implements IListChangeListener {
			@Override
			public void handleListChange(ListChangeEvent event) {
				if (!event.getObservableList().isDisposed()) {
					firePropertyChange(PROP_name);  // update number of children in name
				}
			}
		}

		private final String name; // for user presentation
		private final String path; // for querying server
		private final Class<T> distinctQueryType; // for querying server
		private final GenericType<Set<T>> genericCollectionQueryType; // for querying server
		private final Class<? extends AbstractObservableEntity<T>> observableType;
		private final boolean isGreedyLoad;

		private WritableList sortedEntities;
		private boolean isLoaded = false;
		private IObservableList sections;
		private final int mimimumSectionSize;

//		private final Set newInstances = new HashSet(); // keep track of created instances 

		/**
		 * Private constructor, roots should only be instantiated from this
		 * class (may change if we need to mock roots for testing). Argument
		 * list is a bit long, could probably be shortened with better
		 * understanding of generics.
		 * 
		 * @param nodeName
		 *            human readable, for display in UI listings
		 * @param observableType
		 *            for creating instances in the {@link ItemFactory}
		 *            interface
		 * @param queryPath
		 *            for server query (see {@link #getPath()})
		 * @param distinctElementQueryType
		 *            for server query (see {@link #findAll(String)})
		 * @param genericCollectionQueryType
		 *            for server query (see {@link #findAll()})
		 * @param isGreedyLoad see {@link #isGreedyLoad()}
		 */
		private MyRoot(String nodeName, Class<? extends AbstractObservableEntity<T>> observableType, String queryPath, Class<T> distinctElementQueryType, GenericType<Set<T>> genericCollectionQueryType, boolean isGreedyLoad) {
			this.observableType = observableType;
			this.name = nodeName;
			this.path = queryPath;
			this.distinctQueryType = distinctElementQueryType;
			this.genericCollectionQueryType = genericCollectionQueryType;
//			this.writableSetOfChildren = new SMDWritableSet(children, observableType);
			this.isGreedyLoad = isGreedyLoad;
			this.mimimumSectionSize = 100; // TODO read from user prefs?
		}

		/**
		 * Get all objects of the type that this root handles.
		 * @return {@link List<T>}, possibly empty
		 */
		final public synchronized <O extends ObservableEntity<T>> Set<O> findAll() {
			Set<O> result = new HashSet<O>();
			Set<T> set = get(genericCollectionQueryType);
			for (T serverObject : set) {
				O clientObject = getOrStore(serverObject);
				result.add(clientObject);
			}
			return result;
		}

		/**
		 * Find all objects that this root handles that have a relation to the supplied entity. 
		 * @param <O>
		 * @param entity
		 * @return Collection, possibly empty 
		 */
		final public synchronized <O extends ObservableEntity<T>> Set<O> findAll(SMDIdentity entity) {
			WebResource resource = Client.create(config).resource(getQueryPath(entity));
			Collection<T> collection = resource.accept(MediaType.APPLICATION_JSON).get(genericCollectionQueryType);

			Set<O> result = new HashSet<O>();
			for (T serverObject : collection) {
				O clientObject = getOrStore(serverObject);
				result.add(clientObject);
			}
			return result;
		}
		
		// TODO fix generics, eliminate warning
		private <O extends ObservableEntity<T>> O getOrStore(T serverObject) {
			return (O) cache.getOrStore(serverObject);
		}

		/**
		 * <p>
		 * Returns a {@link WritableSet}. Loads children and updates
		 * {@link #isLoaded} status if connected but not loaded. Do <b>NOT</b>
		 * maintain the same set across sessions; set must be replaced when Root
		 * is reloaded.
		 * </p>
		 * 
		 * <p>
		 * Rationale: the returned set is used as observable input in a viewer.
		 * When the input is assigned, the first thing the viewer does is to
		 * dispose all elements/nodes of the current input, which effectively is
		 * our set. The next time we access it we get an error (
		 * <code>org.eclipse.core.runtime.AssertionFailedException: assertion failed: Getter called on disposed observable</code>
		 * ). Hence we need to replace the old set with a brand new one when we
		 * reassign the input.
		 * </p>
		 * 
		 * @see #dispose()
		 */
		public IObservableList getObservableChildren() {
			if (!isLoaded && (isConnected || isAutoConnect)) {
				JobUtil.run(new MyLoader(this), "Load content: "+name, false);
//				Disable to avoid "Reentrant exception" - but where do we fire the event?
//				firePropertyChange(PROP_name); // update number of children
			}
			return sortedEntities==null || sortedEntities.size()<mimimumSectionSize ? sortedEntities : sections;
		}

		@SuppressWarnings("unchecked")
		private void load() {
			List<ObservableEntity> all = new ArrayList<ObservableEntity>(findAll());
			Collections.sort(all); 
			sortedEntities = new WritableList(all, getType());
			isLoaded = true;
			sortedEntities.addListChangeListener(new MyEntityCollectionListener());
			sections = createSections();
		}

		public String getName() {
			return sortedEntities==null || sortedEntities.isDisposed() ? name : name + " ("+sortedEntities.size()+")";
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

		protected Set<T> get(GenericType<Set<T>> genericCollectionQueryType) {
			try {
				Set<T> set = Client.create(config).resource(getPath()).accept(MediaType.APPLICATION_JSON).get(genericCollectionQueryType);
				isConnected = true;
				return set;
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
//			} else if (isDeleted(entity)) {
//				We delete instantly (at least for now)
//				delete(entity);
			} else {
				update(entity);
			}
			sectionChange(entity);
		}

		/**
		 * CRUD: <b>C</b>reate
		 * @param entity
		 */
		private void create(ObservableEntity entity) {
			assert isNew(entity) : "Not a new entity: "+entity;
			Class<T> type = entity.getPersistentType();
			T echo = resource(getPath()).type(MediaType.APPLICATION_JSON).post(type, entity);
			assert echo.getClass().equals(entity.getClass()) : "Bad post; echo did not return proper type. Expected: "+entity.getClass()+", actual: "+echo.getClass();
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
			assert (isNew(entity) || cache.contains(entity)) : "Deleting uncached entity - should have been cached when read:\n\t"+entity+"#"+entity.hashCode()+"\nCache content:\n"+cache.dump();
			if (isNew(entity)) {
//				newInstances.remove(entity);
			} else {
				String entityPath = getInstancePath(entity.getId());
				resource(entityPath).type(MediaType.APPLICATION_JSON).delete();
				cache.delete(entity);
			}
			if (sortedEntities!=null) {
				sortedEntities.remove(entity);
				sectionRemove(entity);
			}
			
			assert !cache.contains(entity) : "cache not updated - entity still present: "+entity;
		}

		private void sectionAdd(ModelObject element) {
			MySection s = sectionMatch(element);
			if (s!=null) {
				s.add(element);
			}
		}

		private void sectionRemove(ModelObject element) {
			if (sections != null) {
				for (MySection s : getSections()) {
					if (s.remove(element)) {
						break;
					}
				}
			}
		}
		private void sectionChange(ModelObject entity) {
			MySection s = sectionMatch(entity);
			if (s!=null && !s.contains(entity)) {
				sectionRemove(entity);
				s.add(entity);
			}
		}

		private MySection sectionMatch(ModelObject element) {
			if (sections != null) {
				List<MySection> reversedSections = new ArrayList<MySection>(getSections());
				Collections.reverse(reversedSections);
				for (MySection s : reversedSections) {
					if (s.sortsAfterFirstChild(element)) {
						return s;
					}
				}
				throw new IllegalStateException("Cannot find section for element: "+element);
			}
			return null;
		}

		private boolean isNew(ObservableEntity entity) {
			return entity.getId()==null;
		}
		
		/**
		 * Stub, primarily to keep label providers happy. Answer false for the time being.
		 * @return <code>false</code>
		 */
		public boolean isDirty() {
			return false;
		}
		/**
		 * Do <b>NOT</b> maintain the same set of children across sessions; set
		 * must be replaced when Root is reloaded. See comments on
		 * {@link #getObservableChildren()}.
		 */
		public void dispose() {
			sortedEntities = null;
			isLoaded = false;
		}

		/**
		 * CRUD: <b>C</b>reate
		 * @return entity
		 */
		@SuppressWarnings("unchecked")
		@Override
		public T newInstance() {
			return (T) createInstance(observableType);
		}

		/**
		 * CRUD: <b>C</b>reate
		 * @return entity
		 */
		@Override
		public <U extends AbstractObservableEntity> U newInstance(Class<U> type) {
			return createInstance(type);
		}

//		private String newId() {
//			return UUID.randomUUID().toString();
//		}

		private <U extends AbstractObservableEntity> U createInstance(Class<U> type) {
			try {
				U newInstance = type.newInstance();
				newInstance.postCreate();
				newInstance.setName("<new>");
				if (sortedEntities!=null) {
					sortedEntities.add(newInstance);
					sectionAdd(newInstance);
				}
				return newInstance;
			} catch (InstantiationException e) {
				throw new FatalApplicationException("Unable to create new instance of type "+observableType, e);  //$NON-NLS-1$
			} catch (IllegalAccessException e) {
				throw new FatalApplicationException("Unable to create new instance of type "+observableType, e);  //$NON-NLS-1$
			}
		}

		/**
		 * Does this root load all persistent attributes of the instance on
		 * first read? If so, {@link DataSource#inflate(ObservableEntity)} does
		 * not need to do an extra read to fill all properties before editing or
		 * hooking listeners - the instance is already "inflated".
		 * 
		 * @return boolean
		 */
		public boolean isGreedyLoad() {
			return this.isGreedyLoad;
		}

		@Override
		public boolean hasChildren() {
			// return true if lazy load - hitting "expand" will update actual state if no children found
			return isLoaded ? sortedEntities.size()>0 : isLazyRootLoad;
		}

		/**
		 * Find a reasonable balance between sections and children, and create
		 * sections with subsets of children accordingly. This simple algorithm
		 * should work reasonably well up to 100k entries (333*333), if we want
		 * to go beyond that we probably need to add an extra layer.
		 * 
		 * @return {@link IObservableList} of {@link MySection}s
		 */
		private IObservableList createSections() {
			// calculate sizes
			int totalSize = sortedEntities.size();
			double nominalSectionSize = Math.max(Math.sqrt(totalSize), mimimumSectionSize);
			int nofSections = (int) Math.ceil(totalSize/nominalSectionSize );
			int sectionSize = (int) (nofSections==0 ? nominalSectionSize : Math.ceil(totalSize/nofSections));

			// create sections
			IObservableList result = new WritableList();
			for (int s = 0; s < nofSections; s++) {
				int fromIndex = s * sectionSize;
				int toIndex = Math.min(fromIndex + sectionSize, totalSize);
				MySection<T> section = new MySection<T>(this, sortedEntities.subList(fromIndex, toIndex), s==0);
				result.add(section);
			}
			
			// done!
			return result;
		}

		@SuppressWarnings("unchecked")
		private List<MySection> getSections() {
			return sections;
		}
	}

	private class MySection<T extends SMDIdentity> extends AbstractObservable implements ModelObject, ItemFactory<T> {
		
		private final MyRoot<T> root;
		private final IObservableList children;
		private final boolean isFirstSection;
		
		@SuppressWarnings("unchecked")
		private MySection(MyRoot<T> root, List list, boolean isFirstSection) {
			this.root = root;
			this.isFirstSection = isFirstSection;
			
			// Must wrap wrapped list since WritableList updates backed list, which is an unmodifiable subset.
			// If we change this to a WritableSet, the extra wrapping is not required (compare the 
			// internal implementation of WritableList with WritableSet).
			ArrayList wrappedWrapped = new ArrayList(list);
			this.children = new WritableList(wrappedWrapped, null);
			
//			int max = Math.min(10, n.length());
//			String string = n.substring(0, max);
		}

		public boolean sortsAfterFirstChild(ModelObject element) {
			String prospect = element.getName();
			String threshold = isFirstSection ? "" : getFirstChild().getName();
			return prospect.compareTo(threshold) >= 0;
		}

		public boolean contains(ModelObject entity) {
			return getObservableChildren().contains(entity);
		}

		public boolean remove(ModelObject element) {
			return getObservableChildren().remove(element);
		}

		/**
		 * Add if not already found (mimics {@link Set#add(Object)}).
		 * @param element
		 * @return <code>true</code> if element was added
		 */
		public boolean add(ModelObject element) {
			return !contains(element) && getObservableChildren().add(element);
		}

		@Override
		public Object getAdapter(Class adapter) {
			return adapter.isInstance(this) ? this : root.getAdapter(adapter);
		}

		@Override
		public T newInstance() {
			return root.newInstance();
		}

		@Override
		public String getName() {
			ModelObject child1 = getFirstChild();
			String childName1 = child1==null ? "" : child1.getName();
			return childName1 + " >>"; // TODO replace with icon/decorator? 
		}

		@Override
		public IObservableList getObservableChildren() {
			return children;
		}

		@Override
		public boolean hasChildren() {
			return children!=null && !children.isEmpty();
		}

		private ModelObject getFirstChild() {
			// avoid WidgetDisposed exception when shutting down UI
			return children.isDisposed() || children.isEmpty() ? null : (ModelObject) children.get(0);
		}

	}

	@SuppressWarnings("unchecked")
	public List<MyRoot<? extends SMDIdentity>> getRoots() {
		if (roots == null) {
			roots = Arrays.asList(
				new MyRoot<Artist>("Artists", ObservableArtist.class, "/artists", Artist.class, new GenericType<Set<Artist>>() {}, false )
				,new MyRoot<Contributor>("Contributors", ObservableContributor.class, "/contributors", Contributor.class, new GenericType<Set<Contributor>>() {}, true )
				,new MyRoot<Recording>("Recordings", ObservableRecording.class, "/recordings", Recording.class, new GenericType<Set<Recording>>() {}, true )
				,new MyRoot<Release>("Releases", ObservableRelease.class, "/releases", Release.class, new GenericType<Set<Release>>() {}, false ) 
				,new MyRoot<Track>("Tracks", ObservableTrack.class, "/tracks", Track.class, new GenericType<Set<Track>>() {}, true )
				,new MyRoot<PlayableElement>("Playables", ObservablePlayableElement.class, "/playableElements", PlayableElement.class, new GenericType<Set<PlayableElement>>() {}, false )
				,new MyRoot<Work>("Works", ObservableWork.class, "/works", Work.class, new GenericType<Set<Work>>() {}, false)
				,new MyRoot<Label>("Labels", ObservableLabel.class, "/labels", Label.class, new GenericType<Set<Label>>() {}, false)
			);
		}
		return roots;
	}

	/**
	 * Find the {@link MyRoot} for the supplied entity. Fail if no matching root is found. 
	 * @param entity
	 * @return Root
	 */
	@SuppressWarnings("unchecked")
	public <T extends SMDIdentity> MyRoot<T> resolveRoot(T entity) {
		// if number of roots grow, we should probably use a Map<Class, Root> 
		for (MyRoot root : getRoots()) {
			if (root.getType().isInstance(entity)) {
				return root;
			}
		}
		throw new IllegalArgumentException("Cannot resolve root for instance: "+entity);
	}

	@SuppressWarnings("unchecked")
	private List<MyRoot> getVisibleRoots() {
		return resolveRoots(Artist.class, Release.class, Work.class, Label.class);
	}

	/**
	 * Find the {@link MyRoot}s for the supplied types. 
	 * 
	 * @param requestedTypes
	 * @return List<Root>, possibly empty
	 */
	private List<MyRoot> resolveRoots(Class<? extends SMDIdentity>... requestedTypes) {
		List<MyRoot> matches = new ArrayList<MyRoot>();
		for (MyRoot prospect : getRoots()) {
			for (Class requestedType : requestedTypes) {
				if (prospect.getType().equals(requestedType)) {
					matches.add(prospect);
				}
			}
		}
		return matches;
	}

	/**
	 * Find the {@link MyRoot} for the supplied entity type. Fail if no matching
	 * root is found.
	 * 
	 * @param owner
	 * @return Root
	 */
	@SuppressWarnings("unchecked")
	public <T extends SMDIdentity> MyRoot<T> resolveRoot(Class<T> type) {
		for (MyRoot root : getRoots()) {
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

	/**
	 * <p>
	 * Disconnect from {@link DataSource}, clear all contents and all caches,
	 * and fire necessary events. Fail if not connected. After this, the client
	 * should appear empty until it is connected again.
	 * </p>
	 * 
	 * @return the connected state; <code>false</code> unless we threw an
	 *         exception or got reconnected as a result of firing events
	 * @see #connect()
	 * @throws IllegalStateException
	 */
	public boolean disconnect() {
		if (!isConnected()) {
			throw new IllegalStateException("Not connected");
		}
		cache.clear();
		for (MyRoot root : getRoots()) {
			root.dispose();
		}
		setConnected(false);
		return isConnected();
	}

	/**
	 * Connect to the {@link DataSource} and fire necessary events. Fail if
	 * already connected. After this, the client should have content (if there
	 * is any).
	 * 
	 * @return the connected state; <code>true</code> unless we threw an exception
	 * @see #disconnect()
	 * @throws IllegalStateException
	 */
	public boolean connect() {
		if (isConnected) {
			throw new IllegalStateException("Already connected");
		}
		isConnected = true; // do NOT fire events, but tell roots that they can load data
		if (!isLazyRootLoad ) {
			List<MyRoot> visibleRoots = getVisibleRoots();
			MyRoot[] rootsToLoad = visibleRoots.toArray(new MyRoot[visibleRoots.size()]);
			isConnected  = JobUtil.run(new MyLoader(rootsToLoad), "Connect to server", false);
		}

		// now fire an event to notify listeners and refresh input
		firePropertyChange(new PropertyChangeEvent(this, PROP_IS_CONNECTED, false, isConnected()));
		return isConnected();
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
	@Override
	public IObservableList getObservableChildren() {
		return Observables.staticObservableList(getVisibleRoots());
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
		assert shell!=null : "Must have shell!";
		assert entities.length>0 : "Must have at least one entity";
		if (entities.length==1) {
			return new MyPersistor(shell, entities).persistOnProperThread(entities[0]);
		} else {
			return JobUtil.run(shell, new MyPersistor(shell, entities), "Save "+entities.length+" object(s)", false);
		}
	}

	public <T extends SMDIdentity> void delete(ObservableEntity victim) {
		resolveRoot(victim).delete(victim);
	}
	
	public <T extends SMDIdentity> boolean inflate(ObservableEntity<T> shallowEntity) {
		assert cache.contains(shallowEntity) : "Cache does not contain entity to inflate:\n\t"+shallowEntity+"Cache content:\n"+cache.dump();
		MyRoot root = resolveRoot(shallowEntity);
		if (!root.isGreedyLoad()) {
			@SuppressWarnings("unchecked")
			T richEntity = (T) root.read(shallowEntity.getId());
			try {
				cache.merge(richEntity, shallowEntity);
	//			copyHelper.mergeInto(unInflated, inflated, Exposed.class);
	//			PropertyUtils.copyProperties(unInflated, inflated);
			} catch (Exception e) {
				throw new FatalApplicationException("Failed to inflate instance: "+shallowEntity, e);  //$NON-NLS-1$
			}
		}
		return true;
	}
	
	/**
	 * Run an import from specified module (valid modules are defined by server).
	 * @return {@link OperationStatus}
	 */
	public OperationStatus startImport(String module) {
		WebResource resource = getImportPath(module);
		return resource.post(OperationStatus.class);
	}

	/**
	 * Get the import status from specified module (an import must first be started).
	 * @return {@link MediaImportStatus}
	 * @see #startImport(String)
	 */
	public MediaImportStatus getImportStatus(String module) {
		try {
			return getImportPath(module).accept(MediaType.APPLICATION_JSON).get(MediaImportStatus.class);
		} catch (UniformInterfaceException e) {
			if (e.getResponse().getStatus() != 204) {
				throw e;
			}
		}
		MediaImportStatus mediaImportStatus = new MediaImportStatus();
		mediaImportStatus.setStatus(Status.FinishedOk);
		return mediaImportStatus; 
	}

	/**
	 * Cancel import of specified module.
	 */
	public void cancelImport(String module) {
		getImportPath(module).delete();
	}

	private WebResource getImportPath(String module) {
		return Client.create(config).resource(getServerURI()+ "/mediaimportmodules/" + module);
	}
	
	public static ClientConfig newClientConfig() {
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getClasses().add(ClientConfigModule.JSONProvider.class);
		return clientConfig;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	public void initialize(Shell shell) {
		if (isAutoConnect) {
			connect();
		}
	}
	@Override
	public void setAutoConnect(boolean isAutoConnect) {
		this.isAutoConnect = isAutoConnect;
	}
	
	@Override
	public boolean isAutoConnect() {
		return isAutoConnect;
	}
}
