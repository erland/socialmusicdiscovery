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

package org.socialmusicdiscovery.yggdrasil.foundation.content;

import java.beans.PropertyChangeListener;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.jface.util.Util;
import org.eclipse.ui.PlatformUI;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.yggdrasil.foundation.Activator;
import org.socialmusicdiscovery.yggdrasil.foundation.content.DataSource.Root;
import org.socialmusicdiscovery.yggdrasil.foundation.event.AbstractObservable;

import com.google.gson.annotations.Expose;

/**
 * <p>
 * The root abstraction of all persistent elements that we handle in the client.
 * Each subclass implements a client-side version of the corresponding server
 * object, as defined by the interface defined by the class parameter
 * <code>T</code>. Compared to server objects, these instances are observable in
 * the sense that the accept {@link PropertyChangeListener}s and preserve stable
 * collections that can be observed thru the JFace data binding framework (e.g.
 * thru {@link BeansObservables}). The instance keeps track of its "dirty"
 * status, and fires events when the state changes.
 * </p>
 * 
 * <p>
 * <b>Server Interaction</b><br>
 * The client object will receive and transmit all fields annotated with
 * {@link Expose}. Objects can be "inflated" from the initial, "shallow" state -
 * the "shallow" object carries only the information necessary to present it in
 * UI listings (typically its name). Before opening an editor on the object, it
 * is "inflated"; the remaining fields are filled from a fully initialized
 * object that is fetched from the server.
 * </p>
 * 
 * 
 * @author Peer Törngren
 * 
 * @param <T> the core interface that the subclass implements
 */
public abstract class AbstractObservableEntity<T extends SMDIdentity> extends AbstractObservable implements ObservableEntity<T> {
	private DataSource dataSource;

	private transient boolean isInflated = false;

	@Expose
	private String id;

	@Expose
	private String name;
	private final Class rootType;

	private transient boolean isDirty;

	private transient boolean isDirtyEnabled = true;

	public AbstractObservableEntity() {
		super();
		// TODO make static
		rootType = resolveRootType();
	}

	@SuppressWarnings("unchecked")
	private Class<? extends AbstractObservableEntity> resolveRootType() {
		for (Class type : getClass().getInterfaces()) {
			//			Class superclass = type.getSuperclass(); // null!?
			if (SMDIdentity.class.isAssignableFrom(type)) {
				return type;
			}
		}
		return getClass();
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (adapter.isInstance(this)) {
			return this;
		}
		return null;
	}

    public int compareTo(ModelObject o) {
    	return o==null ? -1 : Util.compare(getName(), o.getName());
    }
    
	/**
	 * <p>
	 * Load remaining data by fetching the 'full' object from the server. If all
	 * data is already loaded, method does nothing. Subclass must not override 
	 * this method, but can add specific behavior by overriding {@link #postInflate()}. 
	 * </p>
	 * <p>
	 * Rationale: when an object is first fetched from the server, it is only
	 * loaded with minimal data to make it presentable in UI listings - a
	 * typical object is only loaded with its 'name' attribute. When we want to
	 * edit the object, we fetch the remaining data from the server.
	 * </p>
	 */
	public final void inflate() {
		if (!isInflated()) {
			isInflated = getDataSource().inflate(this);
			postInflate();
		}
	}

	/**
	 * Do any post-processing after basic inflate. This method is called after
	 * {@link #inflate()}, if the instance was inflated. Default method does
	 * nothing, subclasses should override as necessary, typically to load any
	 * properties not loaded by the default inflation.
	 * @see #postCreate()
	 */
	protected void postInflate() {
		// no-op
	}

	/**
	 * <p>
	 * Do any processing necessary after creating a new instance. Since we need
	 * to run parameter-free constructors, we may not be able to do all we want
	 * in the constructor. After running this method, the instance is ready for
	 * use.
	 * </p>
	 * 
	 * <p>
	 * Note: this method should <b>only</b> be called by {@link Root}, subclasses 
	 * or test classes after creating a new instance. It must <b>not</b> be 
	 * called from the constructor, but after creating a new instance that needs 
	 * to be hooked up with listeners etc the same way an existing instance is 
	 * hooked up after being inflated.
	 * </p>
	 * @see #postInflate()
	 */
	public void postCreate() {
		isInflated = true;
		isDirty = true;
	}
	
	protected DataSource getDataSource() {
		assert !(dataSource==null && Activator.getDefault()==null) : "DataSource not initialized and workbench not running";
		return dataSource==null ? Activator.getDefault().getDataSource() : dataSource;
	}

	/**
	 * Initializer for exclusive and one-time use for unit testing.
	 * 
	 * @param dataSource
	 */
	/* package */ void setTestDataSource(DataSource dataSource) {
		if (this.dataSource!=null) {
			throw new IllegalStateException("DataSource already initialized");
		}
		if (PlatformUI.isWorkbenchRunning()) {
			throw new IllegalStateException("Must not set data source with workbench running - this method is ONLY for use while testing");
		}
		this.dataSource = dataSource;
	}
	/**
	 * Convenience method for subclasses. 
	 * @return {@link Root} for this instance
	 */
	protected Root<T> getRoot() {
		return (Root<T>) getDataSource().resolveRoot(this);
	}

	@SuppressWarnings("unchecked")
	protected Class<? extends AbstractObservableEntity> getGenericType() {
		for (Type type : getClass().getGenericInterfaces()) {
			if (type instanceof Class) {
				Class<? extends AbstractObservableEntity> genericClass = (Class<? extends AbstractObservableEntity>) type;
				if (SMDIdentity.class.isAssignableFrom(genericClass)) {
					return genericClass;
				}
			}
		}
		return getClass(); // emergency exit
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	/**
	 * Default implementation returns an empty collection.
	 * Subclasses are expected to override as necessary.
	 * 
	 * @return {@link IObservableSet} (empty)
	 * @see Observables#emptyObservableList()
	 */
	@Override
	public IObservableList getObservableChildren() {
		return Observables.emptyObservableList();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		firePropertyChange(PROP_id, this.id, this.id = id);
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		firePropertyChange(PROP_name, this.name, this.name = name);
	}

	@Override
	public String getTypeName() {
		return rootType.getSimpleName();
	}

	/**
	 * Is this instance fully loaded? If not, it will only hold fundamental
	 * identity properties like {@link #getId()} and {@link #getName()}.
	 * 
	 * @return boolean
	 * @see #inflate()
	 */
	public boolean isInflated() {
		return isInflated;
	}

	@Override
	public String toString() {
		String n = getName();
		if (n==null) {
			n=getId();
		}
		if (n==null) {
			n="@"+hashCode();
		}
		return getClass().getSimpleName()+"/'"+n+"'";
	}
	
	/**
	 * Convenience method, primarily intended for subclasses that implement
	 * {@link #postInflate()} to inflate dependent entities.
	 * 
	 * @param entities
	 */
	protected void inflateAll(Collection<? extends AbstractObservableEntity> entities) {
		for (AbstractObservableEntity entity : entities) {
			entity.inflate();
		}
	}
	
	/**
	 * Default implementation returns an empty set. Subclasses should override as necessary.
	 */
	public Set<? extends ObservableEntity> getRemovedDependents() {
		return Collections.emptySet();
	}

	/**
	 * Default implementation returns an empty set. Subclasses should override as necessary.
	 * Returned set may include new, modified and unmodified entities.
	 */
	public Set<? extends ObservableEntity> getSaveableDependents() {
		return Collections.emptySet();
	}

	public boolean isDirty() {
		return isDirty;
	}

	protected boolean isDirtyEnabled() {
		return isDirtyEnabled;
	}

	/**
	 * Mark instance as dirty (or not). Method must be called whenever the
	 * persistent state of this instance changes. Subclasses may disable dirty
	 * handling while setting derived attributes that should fire property
	 * change events but not alter the dirty status.
	 * 
	 * @param isDirty
	 * @see #setDirtyEnabled(boolean)
	 */
	@Override
	public void setDirty(boolean isDirty) {
		if (isDirtyEnabled) {
			super.firePropertyChange(ObservableEntity.PROP_dirty, this.isDirty, this.isDirty = isDirty);
		}
	}

	/**
	 * Enable or disable dirty handling. Subclasses may need to disable dirty
	 * handling while setting derived attributes that should fire property
	 * change events but not alter the dirty status.
	 * 
	 * @param isDirtyEnabled
	 */
	protected void setDirtyEnabled(boolean isDirtyEnabled) {
		this.isDirtyEnabled = isDirtyEnabled;
	}

	protected void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue) {
		setDirty(true); // call before firing "real" change to let tooltip render dirty status
		super.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
	}

	protected void fireIndexedPropertyChange(String propertyName, int index, int oldValue, int newValue) {
		setDirty(true); // call before firing "real" change to let tooltip render dirty status
		super.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
	}

	protected void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
		setDirty(true); // call before firing "real" change to let tooltip render dirty status
		super.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
	}

	protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
		setDirty(true); // call before firing "real" change to let tooltip render dirty status
		super.firePropertyChange(propertyName, oldValue, newValue);
	}

	protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
		setDirty(true); // call before firing "real" change to let tooltip render dirty status // call before firing "real" change to let tooltip render dirty status
		super.firePropertyChange(propertyName, oldValue, newValue);
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		setDirty(true); // call before firing "real" change to let tooltip render dirty status
		super.firePropertyChange(propertyName, oldValue, newValue);
	}
}
