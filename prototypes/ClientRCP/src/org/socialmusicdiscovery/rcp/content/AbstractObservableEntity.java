package org.socialmusicdiscovery.rcp.content;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.socialmusicdiscovery.rcp.Activator;
import org.socialmusicdiscovery.rcp.event.AbstractObservable;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import com.google.gson.annotations.Expose;

/**
 * Work in progress ... you have been warned.
 * 
 * @author Peer Törngren
 *
 * @param <T>
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
	 * data is already loaded, method does nothing.
	 * </p>
	 * <p>
	 * Rationale: when an object is first fetched from the server, it is only
	 * loaded with minimal data to make it presentable in UI listings - a
	 * typical object is only loaded with its 'name' attribute. When we want to
	 * edit the object, we fetch the remaining data from the server.
	 * </p>
	 */
	public void inflate() {
		if (!isInflated) {
			isInflated = Activator.getDefault().getDataSource().inflate(this);
		}
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolTipText() {
		// TODO add some meaningful info (from subclasses?)
		return getId()+": "+getName();
	}

	/**
	 * Default implementation returns an empty collection.
	 * Subclasses are expected to override as necessary.
	 * 
	 * @return {@link IObservableList} (empty)
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

	public boolean isDirty() {
		return isDirty;
	}

	/**
	 * Mark instance as dirty. Method must be called whenever the persistent state of this instance changes.
	 * TODO Hook listeners in collections to detect changes made directly to collections (thru {@link WritableList}?) 
	 * @param isDirty
	 */
	public void setDirty(boolean isDirty) {
		super.firePropertyChange(PROP_isDirty, this.isDirty, this.isDirty = isDirty);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+"/'"+getName()+"'";
	}
	
	protected void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue) {
		super.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
		setDirty(true);
	}

	protected void fireIndexedPropertyChange(String propertyName, int index, int oldValue, int newValue) {
		super.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
		setDirty(true);
	}

	protected void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
		super.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
		setDirty(true);
	}

	protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
		super.firePropertyChange(propertyName, oldValue, newValue);
		setDirty(true);
	}

	protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
		super.firePropertyChange(propertyName, oldValue, newValue);
		setDirty(true);
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		super.firePropertyChange(propertyName, oldValue, newValue);
		setDirty(true);
	}

}
