package org.socialmusicdiscovery.rcp.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ObservableImpl implements Observable  {
	
	final private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/* (non-Javadoc)
	 * @see org.socialmusicdiscovery.rcp.event.Observable#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.socialmusicdiscovery.rcp.event.Observable#addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
	 */
	@Override
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(propertyName, listener);
	}

	/* (non-Javadoc)
	 * @see org.socialmusicdiscovery.rcp.event.Observable#fireIndexedPropertyChange(java.lang.String, int, boolean, boolean)
	 */
	@Override
	public void fireIndexedPropertyChange(String propertyName, int index,
			boolean oldValue, boolean newValue) {
		pcs.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
	}

	/* (non-Javadoc)
	 * @see org.socialmusicdiscovery.rcp.event.Observable#fireIndexedPropertyChange(java.lang.String, int, int, int)
	 */
	@Override
	public void fireIndexedPropertyChange(String propertyName, int index,
			int oldValue, int newValue) {
		pcs.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
	}

	/* (non-Javadoc)
	 * @see org.socialmusicdiscovery.rcp.event.Observable#fireIndexedPropertyChange(java.lang.String, int, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void fireIndexedPropertyChange(String propertyName, int index,
			Object oldValue, Object newValue) {
		pcs.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
	}

	/* (non-Javadoc)
	 * @see org.socialmusicdiscovery.rcp.event.Observable#firePropertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void firePropertyChange(PropertyChangeEvent evt) {
		pcs.firePropertyChange(evt);
	}

	/* (non-Javadoc)
	 * @see org.socialmusicdiscovery.rcp.event.Observable#firePropertyChange(java.lang.String, boolean, boolean)
	 */
	@Override
	public void firePropertyChange(String propertyName, boolean oldValue,
			boolean newValue) {
		pcs.firePropertyChange(propertyName, oldValue, newValue);
	}

	/* (non-Javadoc)
	 * @see org.socialmusicdiscovery.rcp.event.Observable#firePropertyChange(java.lang.String, int, int)
	 */
	@Override
	public void firePropertyChange(String propertyName, int oldValue,
			int newValue) {
		pcs.firePropertyChange(propertyName, oldValue, newValue);
	}

	/* (non-Javadoc)
	 * @see org.socialmusicdiscovery.rcp.event.Observable#firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		pcs.firePropertyChange(propertyName, oldValue, newValue);
	}

	/* (non-Javadoc)
	 * @see org.socialmusicdiscovery.rcp.event.Observable#getPropertyChangeListeners()
	 */
	@Override
	public PropertyChangeListener[] getPropertyChangeListeners() {
		return pcs.getPropertyChangeListeners();
	}

	/* (non-Javadoc)
	 * @see org.socialmusicdiscovery.rcp.event.Observable#getPropertyChangeListeners(java.lang.String)
	 */
	@Override
	public PropertyChangeListener[] getPropertyChangeListeners(
			String propertyName) {
		return pcs.getPropertyChangeListeners(propertyName);
	}

	/* (non-Javadoc)
	 * @see org.socialmusicdiscovery.rcp.event.Observable#hasListeners(java.lang.String)
	 */
	@Override
	public boolean hasListeners(String propertyName) {
		return pcs.hasListeners(propertyName);
	}

	/* (non-Javadoc)
	 * @see org.socialmusicdiscovery.rcp.event.Observable#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.socialmusicdiscovery.rcp.event.Observable#removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
	 */
	@Override
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(propertyName, listener);
	}

}
