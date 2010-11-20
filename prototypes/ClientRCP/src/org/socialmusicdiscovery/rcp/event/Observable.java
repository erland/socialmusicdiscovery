package org.socialmusicdiscovery.rcp.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public interface Observable {

	public abstract void addPropertyChangeListener(
			PropertyChangeListener listener);

	public abstract void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener);

	public abstract void fireIndexedPropertyChange(String propertyName,
			int index, boolean oldValue, boolean newValue);

	public abstract void fireIndexedPropertyChange(String propertyName,
			int index, int oldValue, int newValue);

	public abstract void fireIndexedPropertyChange(String propertyName,
			int index, Object oldValue, Object newValue);

	public abstract void firePropertyChange(PropertyChangeEvent evt);

	public abstract void firePropertyChange(String propertyName,
			boolean oldValue, boolean newValue);

	public abstract void firePropertyChange(String propertyName, int oldValue,
			int newValue);

	public abstract void firePropertyChange(String propertyName,
			Object oldValue, Object newValue);

	public abstract PropertyChangeListener[] getPropertyChangeListeners();

	public abstract PropertyChangeListener[] getPropertyChangeListeners(
			String propertyName);

	public abstract boolean hasListeners(String propertyName);

	public abstract void removePropertyChangeListener(
			PropertyChangeListener listener);

	public abstract void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener);

}