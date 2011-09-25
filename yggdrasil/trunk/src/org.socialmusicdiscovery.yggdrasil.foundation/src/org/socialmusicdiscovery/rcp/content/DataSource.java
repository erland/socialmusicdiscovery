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

package org.socialmusicdiscovery.rcp.content;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.swt.widgets.Shell;
import org.socialmusicdiscovery.rcp.Activator;
import org.socialmusicdiscovery.server.api.OperationStatus;
import org.socialmusicdiscovery.server.api.management.mediaimport.MediaImportStatus;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;

/**
 * @author Peer Törngren
 *
 */
public interface DataSource extends ModelObject {
	
	/** The name of the squeezebox server module, as defined by the SMD server. Use for running imports. */
	String MODULE_SQUEEZEBOXSERVER = "squeezeboxserver"; //$NON-NLS-1$
	
	/**
	 * The ID of the extension point where the implementation must be registered.
	 * Only one implementation can be registered in an application.
	 */
	String ExtensionID = Activator.PLUGIN_ID+".datasource"; //$NON-NLS-1$

	/** Property name of event that is fired when the {@link #isConnected()} property changes. */
	String PROP_IS_CONNECTED = "isConnected"; //$NON-NLS-1$

	/**
	 * The "home" of a specific type of entity.
	 *
	 * @param <T> the common entity interface that this instance operates on
	 */
	interface Root <T extends SMDIdentity> extends ModelObject, ItemFactory<T> {

		/**
		 * Get all objects of the type that this root handles. 
		 * @return {@link List<T>}, possibly empty
		 */
		<O extends ObservableEntity<T>> Set<O> findAll();
		
		/**
		 * Find all objects that this root handles that have a relation to the supplied entity. 
		 * @param <O>
		 * @param entity
		 * @return Collection, possibly empty 
		 */
		<O extends ObservableEntity<T>> Set<O> findAll(SMDIdentity entity);

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
		IObservableList getObservableChildren();
		
		/**
		 * Get the SMD base interface type that this instance handles. 
		 * @return T
		 */
		Class<T> getType();

		/**
		 * Stub, primarily to keep label providers happy. 
		 * @return boolean
		 */
		boolean isDirty();

		/**
		 * Do <b>NOT</b> maintain the same set of children across sessions; set
		 * must be replaced when Root is reloaded. See comments on
		 * {@link #getObservableChildren()}.
		 */
		void dispose();

		/**
		 * Does this root load all persistent attributes of the instance on
		 * first read? If so, {@link DataSource#inflate(ObservableEntity)} does
		 * not need to do an extra read to fill all properties before editing or
		 * hooking listeners - the instance is already "inflated".
		 * 
		 * @return boolean
		 */
		boolean isGreedyLoad();	


	}
	
	/**
	 * <p>
	 * A subset of a {@link Root}, primarily created in order to improve
	 * usability and viewer performance; maintaining a large observable
	 * collections (+1k) can take significant time - with a few thousnad
	 * entries, the UI simply becomes unusable.
	 * </p>
	 * 
	 * <p>
	 * Each Section holds an alphabetical subset of the root's entities; think
	 * of this as a page in a dictionary, where each page lists the first word
	 * of the page.
	 * </p>
	 * <p>
	 * Section size is determined by the {@link Root} that creates it, the
	 * {@link Root} is responsible for creating the section with a valid subset
	 * of elements. The first element will determine the "section threshold"
	 * that is used to match new entries against the section. There is no "end";
	 * a matching section is found by traversing the sections backwards and
	 * finding the first section where the threshold is lower than the entry
	 * (see {@link #sectionMatch(ModelObject)}).
	 * </p>
	 * <p>
	 * Sections are updated when root contents change, and also when an existing
	 * instance is saved: if the name has changed, the instance may have to be
	 * relocated to a new section.
	 * </p>
	 * 
	 * @param <T>
	 *            the common entity interface that this instance operates on
	 */
	interface Section<T extends SMDIdentity> extends ModelObject, ItemFactory<T> {

		/**
		 * Is supplied entity part of this section? 
		 * @param entity
		 * @return boolean
		 */
		boolean contains(ModelObject entity);
		
		/**
		 * Remove supplied element, if it exists. Return boolean to indicate if
		 * the action had any effect (compare to
		 * {@link Collection#remove(Object)}).
		 * 
		 * @param element
		 * @return boolean
		 */
		boolean remove(ModelObject element);

		/**
		 * Add if not already found (mimics {@link Set#add(Object)}).
		 * @param element
		 * @return <code>true</code> if element was added
		 */
		boolean add(ModelObject element);

		/**
		 * Get the name of the first element in this section.
		 * @return String
		 */
		String getFirstName();

		/**
		 * @return {@link Root} that this section belongs to
		 */
		Root getRoot();
	}

	/**
	 * @return {@link List} of {@link Root}s
	 */
	List<? extends Root> getRoots();

	/**
	 * Find the {@link Root} for the supplied entity. Fail if no matching root is found. 
	 * @param entity
	 * @return Root
	 */
	<T extends SMDIdentity> Root<T> resolveRoot(T entity);

	/**
	 * Find the {@link Root} for the supplied entity type. Fail if no matching
	 * root is found.
	 * 
	 * @param owner
	 * @return Root
	 */
	<T extends SMDIdentity> Root<T> resolveRoot(Class<T> type);
	
	/**
	 * Is the server connected? The exact meaning of this may vary with implementation,
	 * a RESTful connection does not really have a "connected" state. It may still be 
	 * meaningful to mimic this state in order to disconnect from one server and connect 
	 * to another, or to reconnect to the same server after a failure.
	 *   
	 * @return boolean
	 */
	boolean isConnected();
	
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
	boolean disconnect();
	
	/**
	 * Connect to the {@link DataSource} and fire necessary events. Fail if
	 * already connected. After this, the client should have content (if there
	 * is any).
	 * 
	 * @return the connected state; <code>true</code> unless we threw an exception
	 * @see #disconnect()
	 * @throws IllegalStateException
	 */
	boolean connect();
	
	/**
	 * Save changes.
	 * @param shell
	 * @param entities
	 * @return <code>true</code> if saved OK, <code>false</code> if not
	 */
	boolean persist(Shell shell, ObservableEntity... entities);

	/**
	 * Ask server to delete the supplied entity from persistent store.
	 * 
	 * @param victim
	 */
	<T extends SMDIdentity> void delete(ObservableEntity victim);

	/**
	 * Fetch and load all data for the supplied element.
	 * @param shallowEntity
	 * @return 
	 */
	<T extends SMDIdentity> boolean inflate(ObservableEntity<T> shallowEntity);

	/**
	 * Run an import from specified module (valid modules are defined by server).
	 * @return {@link OperationStatus}
	 */
	OperationStatus startImport(String module);

	/**
	 * Get the import status from specified module (an import must first be started).
	 * @return {@link MediaImportStatus}
	 * @see #startImport(String)
	 */
	MediaImportStatus getImportStatus(String module);

	/**
	 * Cancel import of specified module.
	 */
	void cancelImport(String module);

	/**
	 * Initialize the data source using the supplied shell for any UI elements
	 * (eg progress dialogs).
	 * 
	 * @param shell
	 */
	void initialize(Shell shell);

	/**
	 * Set this instance to auto-connect or not. The setting is typically 
	 * read from a preference store and set when instantiating the object.
	 * 
	 * @param b
	 */
	void setAutoConnect(boolean b);
	
	/**
	 * Is this instance set to auto-connect to the server?
	 * If so, there is no need for an explicit call to {@link #connect()}.
	 *  
	 * @return boolean
	 */
	boolean isAutoConnect();

}
