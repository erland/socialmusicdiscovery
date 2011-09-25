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

package org.socialmusicdiscovery.yggdrasil.foundation.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.socialmusicdiscovery.yggdrasil.foundation.error.FatalApplicationException;
import org.socialmusicdiscovery.yggdrasil.foundation.util.GenericWritableList;
import org.socialmusicdiscovery.yggdrasil.foundation.util.GenericWritableSet;

/**
 * Basic implementation that delegates all calls to an internal
 * {@link PropertyChangeSupport} instance, extending the public interface with
 * some internal convenience methods for subclasses that need to fire events or
 * manage listeners.
 * 
 * @author Peer Törngren
 * 
 */
public abstract class AbstractObservable implements Observable {

	final private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public PropertyChangeListener[] getPropertyChangeListeners() {
		return pcs.getPropertyChangeListeners();
	}

	@Override
	public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
		return pcs.getPropertyChangeListeners(propertyName);
	}

	@Override
	public boolean hasListeners(String propertyName) {
		return pcs.hasListeners(propertyName);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(propertyName, listener);
	}

	protected void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue) {
		pcs.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
	}

	protected void fireIndexedPropertyChange(String propertyName, int index, int oldValue, int newValue) {
		pcs.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
	}

	protected void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
		pcs.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
	}

	protected void firePropertyChange(PropertyChangeEvent evt) {
		pcs.firePropertyChange(evt);
	}

	protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
		pcs.firePropertyChange(propertyName, oldValue, newValue);
	}

	protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
		pcs.firePropertyChange(propertyName, oldValue, newValue);
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		pcs.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Fire {@link PropertyChangeEvent}s for all supplied property names. No old
	 * or new values are sent.
	 * 
	 * @param propertyNames
	 */
	protected void firePropertyChange(String... propertyNames) {
		for (String propertyName : propertyNames) {
			firePropertyChange(propertyName);
		}
	}
	
	public void firePropertyChange(String propertyName) {
		PropertyChangeEvent e = new PropertyChangeEvent(this, propertyName, null, null);
		pcs.firePropertyChange(e);
	}

	/**
	 * Update content of existing set, retaining the instance. Convenience method to eliminate 
	 * type conformance warnings from subclasses. Could probably be fixed by other complier settings 
	 * or smarter signature.
	 * @param propertyName
	 * @param existingSet
	 * @param newContent
	 */
	@SuppressWarnings("unchecked")
	protected <T> void updateSet(String propertyName, GenericWritableSet<T> existingSet, Collection<? extends T> newContent) {
		updateSet(propertyName, (Set<T>)existingSet, newContent);
	}
	
	/**
	 * Update content of existing set, retaining the instance.
	 * @param propertyName
	 * @param existingSet
	 * @param newContent
	 */
	protected <T> void updateSet(String propertyName, Set<T> existingSet, Collection<? extends T> newContent) {
		try {
			Set<T> oldContent = existingSet.getClass().newInstance();
			oldContent.addAll(existingSet);
			existingSet.retainAll(newContent);
			existingSet.addAll(newContent);
			firePropertyChange(propertyName, oldContent, existingSet);
		} catch (InstantiationException e) {
			throw new FatalApplicationException("Unable to clone set: "+existingSet, e);  //$NON-NLS-1$
		} catch (IllegalAccessException e) {
			throw new FatalApplicationException("Unable to clone set: "+existingSet, e);  //$NON-NLS-1$
		}
	}

	/**
	 * Update content of existing list, retaining the instance. Convenience method to eliminate 
	 * type conformance warnings from subclasses. Could probably be fixed by other complier settings 
	 * or smarter signature.
	 * @param propertyName
	 * @param existingList
	 * @param newContent
	 */
	@SuppressWarnings("unchecked")
	protected <T> void updateList(String propertyName, GenericWritableList<T> existingList, Collection<T> newContent) {
		updateList(propertyName, (List<T>) existingList, newContent);
	}
	/**
	 * Update content of existing list, retaining the instance.
	 * @param propertyName
	 * @param existingList
	 * @param newContent
	 */
	protected <T> void updateList(String propertyName, List<T> existingList, Collection<T> newContent) {
		try {
			List<T> oldContent = existingList.getClass().newInstance();
			oldContent.addAll(existingList);
			existingList.clear();
			existingList.addAll(newContent);
			firePropertyChange(propertyName, oldContent, existingList);
		} catch (InstantiationException e) {
			throw new FatalApplicationException("Unable to clone list: "+existingList, e);  //$NON-NLS-1$
		} catch (IllegalAccessException e) {
			throw new FatalApplicationException("Unable to clone list: "+existingList, e);  //$NON-NLS-1$
		}
	}
}
