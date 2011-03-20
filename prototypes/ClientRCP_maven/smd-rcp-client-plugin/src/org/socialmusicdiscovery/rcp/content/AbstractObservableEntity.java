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

import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.socialmusicdiscovery.rcp.Activator;
import org.socialmusicdiscovery.rcp.event.AbstractObservable;
import org.socialmusicdiscovery.rcp.util.TextUtil;
import org.socialmusicdiscovery.rcp.util.Util;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import com.google.gson.annotations.Expose;

/**
 * <p>
 * The root abstraction of all persistent elements that we edit in the client.
 * Each subclass implements a client-side version of the corresponding server
 * object, as defined by the interface defined by the class parameter
 * <code>T</code>. Compared to server objects, these instances are observable in
 * the sense that the accept {@link PropertyChangeListener}s and preserve stable
 * collections that can be observed thru the JFace data binding framework (e.g.
 * thru {@link BeansObservables}).
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
 * <p>
 * <b>Editor Features</b><br>
 * The client object keeps track of its "dirty" status, and fires events when
 * the state changes. Before opening the instance in an editor, the editor makes
 * a "backup" of the instance in order to do a "restore" if user aborts changes.
 * </p>
 * 
 * @author Peer Törngren
 * 
 * @param <T> the interface the subclass implements
 */
public abstract class AbstractObservableEntity<T extends SMDIdentity> extends AbstractObservable implements ObservableEntity<T> {
	private static final String PROP_id = "id"; //$NON-NLS-1$
	private static final String PROP_isDirty = "dirty"; //$NON-NLS-1$
	
	private boolean isDirty;
	private boolean isInflated = false;
	
	@Expose
	private String id;

	@Expose
	private String name;

	@Override
	public Object getAdapter(Class adapter) {
		if (adapter.isInstance(this)) {
			return this;
		}
		return null;
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
		if (!isInflated) {
			isInflated = getDataSource().inflate(this);
			postInflate();
		}
	}

	/**
	 * Do any post-processing after basic inflate.
	 * This method is called after {@link #inflate()}, 
	 * if the instance was inflated. Default method does 
	 * nothing, subclasses should override as necessary, 
	 * typically to load any properties not loaded by the 
	 * default infaltion.  
	 */
	protected void postInflate() {
		// no-op
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		String isModified = isDirty() ? " {modified}" : ""; 
		String typeName = TextUtil.getText(getGenericType());
		return "["+typeName+"] "+getName()+isModified ;
	}

	/**
	 * Convenience method for subclasses. 
	 * @return {@link DataSource}
	 */
	protected DataSource getDataSource() {
		return Activator.getDefault().getDataSource();
	}

	private Class getGenericType() {
		for (Type type : getClass().getGenericInterfaces()) {
			if (type instanceof Class) {
				Class genericClass = (Class) type;
				if (SMDIdentity.class.isAssignableFrom(genericClass)) {
					return genericClass;
				}
			}
		}
		return getClass(); // emergency exit
	}

	/**
	 * Default implementation returns an empty collection.
	 * Subclasses are expected to override as necessary.
	 * 
	 * @return {@link IObservableSet} (empty)
	 * @see Observables#emptyObservableList()
	 */
	@Override
	public IObservableSet getObservableChildren() {
		return Observables.emptyObservableSet();
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

	public boolean isDirty() {
		return isDirty;
	}
	
	/**
	 * Create a backup of the entity. Backup only holds the persistent data.
	 * @return {@link AbstractObservableEntity}
	 * @see #restore(AbstractObservableEntity)
	 */
	public AbstractObservableEntity backup() {
		AbstractObservableEntity backup = new CopyHelper().copy(this, Expose.class);
		assertBackup(backup);
		return backup;
	}

	/**
	 * Restore state from a backup of this entity. Backup only holds the persistent data.
	 * @see #backup()
	 */
	public void restore(AbstractObservableEntity backup) {
		assertBackup(backup);
		new CopyHelper().mergeInto(this, backup, Expose.class);
		refreshExposedProperties();
		// fire dirty AFTER merge to make sure name changes are updated in 
		// label providers (the copy does not fire any events)
		setDirty(false); 
	}

	private void refreshExposedProperties() {
		for (Field f : Util.getAllFields(getClass(), Expose.class)) {
			firePropertyChange(f.getName());
		};
	}

	/**
	 * Assert that backup is a legal clone of this instance.
	 * @param backup
	 */
	private void assertBackup(AbstractObservableEntity backup) {
		assert backup.getId().endsWith(getId()) : "Bad id: "+backup+". Backup="+backup.getId()+", this="+getId();
		assert backup.getClass()==getClass() : "Bad class: "+backup+". Backup="+backup.getClass()+", this="+getClass();
	}

	
	/**
	 * Mark instance as dirty (or not). Method must be called whenever the persistent state of this instance changes.
	 * TODO Hook listeners in collections to detect changes made directly to collections (thru {@link WritableList}?) 
	 * @param isDirty
	 */
	@Override
	public void setDirty(boolean isDirty) {
		super.firePropertyChange(PROP_isDirty, this.isDirty, this.isDirty = isDirty);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+"/'"+getName()+"'";
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

}
