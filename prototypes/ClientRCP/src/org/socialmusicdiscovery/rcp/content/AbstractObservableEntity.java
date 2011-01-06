package org.socialmusicdiscovery.rcp.content;

import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.socialmusicdiscovery.rcp.Activator;
import org.socialmusicdiscovery.rcp.content.DataSource.Root;
import org.socialmusicdiscovery.rcp.error.FatalApplicationException;
import org.socialmusicdiscovery.rcp.error.NotYetImplementedException;
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
public abstract class AbstractObservableEntity<T extends SMDIdentity> extends AbstractObservable implements ObservableEntity {
	private static final String PROP_id = "id"; //$NON-NLS-1$
	
	@Expose
	private String id;

	@Expose
	private String name;
	
	public boolean isDirty() {
		throw new NotYetImplementedException();
	}
	
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
		// FIXME - cannot open editor from popup menu since no root is set on secondary items
		// cannot depend on "root" being set, need to look up root
		DataSource dataSource = Activator.getDefault().getDataSource();
		Root root = dataSource.resolveRoot(this);
		SMDIdentity inflated = root.find(getId());
		try {
			// Only copy properties declared on interface or by @Expose annotation
			PropertyUtils.copyProperties(this, inflated);
		} catch (Exception e) {
			throw new FatalApplicationException("Failed to inflate instance: "+this, e);  //$NON-NLS-1$
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
	 * Subclasses are expected to override.
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

	@Override
	public String toString() {
		return getClass().getSimpleName()+"/'"+getName()+"'";
	}

}
