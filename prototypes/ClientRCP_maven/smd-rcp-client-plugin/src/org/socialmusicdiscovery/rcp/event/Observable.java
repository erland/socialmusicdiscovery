package org.socialmusicdiscovery.rcp.event;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * An instance that can be observed by a {@link PropertyChangeListener}.
 * This is a vital but not crucial enabler for the JFace databinding framework, 
 * but is also useful for other purposes.   
 *  
 * @author Peer Törngren
 *
 */
public interface Observable {

	/** @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener) */
	void addPropertyChangeListener(PropertyChangeListener listener);

	/** @see PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener) */
	void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

	/** @see PropertyChangeSupport#getPropertyChangeListeners() */
	PropertyChangeListener[] getPropertyChangeListeners();

	/** @see PropertyChangeSupport#getPropertyChangeListeners(String) */
	PropertyChangeListener[] getPropertyChangeListeners(String propertyName);

	/** @see PropertyChangeSupport#hasListeners(String) */
	boolean hasListeners(String propertyName);

	/** @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener) */
	void removePropertyChangeListener(PropertyChangeListener listener);

	/** @see PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener) */
	void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

}