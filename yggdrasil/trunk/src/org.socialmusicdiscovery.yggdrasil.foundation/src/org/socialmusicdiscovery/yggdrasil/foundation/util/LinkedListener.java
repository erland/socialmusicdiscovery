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

package org.socialmusicdiscovery.yggdrasil.foundation.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.socialmusicdiscovery.yggdrasil.foundation.error.FatalApplicationException;
import org.socialmusicdiscovery.yggdrasil.foundation.event.Observable;
import org.socialmusicdiscovery.yggdrasil.foundation.util.ChangeMonitor.PropertyData;

/**
 * <p>
 * A listener that can be linked with other similar listeners to produce a chain
 * of listeners representing a complex property name like
 * "father.mother.children". Whenever element in the chain changes, listeners
 * are released and re-attached so that any change anywhere in the chain fires a
 * {@link PropertyChangeEvent}.
 * </p>
 * 
 * <p>
 * Since some links may be collections, the result isn't really a "chain", but
 * rather some kind of tree, where the root link may expand to zero, one or any
 * number of leaves.
 * </p>
 * 
 * <p>
 * This class is internal and designed for use with {@link ChangeMonitor}. It
 * may or may not work in other contexts.
 * </p>
 * 
 * @author Peer Törngren
 */
/* package */ class LinkedListener implements PropertyChangeListener {

	private final Runnable runner;
	private final PropertyData data;
	private final List<PropertyData> tail;
	private final Set<LinkedListener> links = new HashSet<LinkedListener>();
	private final  Observable observed;

	public LinkedListener(Observable observable, List<PropertyData> allData, Runnable runner) {
		assert !allData.isEmpty() : "Must have data to register listeners";
		this.runner = runner;
		this.data = allData.get(0);
		this.tail = allData.subList(1, allData.size());
		this.observed = observable;
		
		assert tail.isEmpty() || !data.isAnyProperty : "anyProperty set on intermediate element: "+allData;
		assert data.isAnyProperty || data.propertyName!=null : "No property name";
		assert data.isAnyProperty || data.propertyType!=null : "No property type";

		if (data.isAnyProperty) {
			observable.addPropertyChangeListener(this);
		} else {
			observable.addPropertyChangeListener(data.propertyName, this);
			chain(observable, tail, runner);
		}
	}

	@SuppressWarnings("unchecked")
	private void chain(Observable observable, List<PropertyData> chain, Runnable runner) {
		try {
			Object property = PropertyUtils.getProperty(observable, data.propertyName);
			if (property!=null && !chain.isEmpty()) {
				if (data.isCollection) {
					for (Observable o : ((Collection<Observable>) property)) {
						createListener(o, chain, runner);
					}
				} else {
					createListener((Observable) property, chain, runner);
				}
			}
		} catch (Exception e) {
			throw new FatalApplicationException("Unable to hook property '"+data.propertyName+"' from bean: "+observable, e);  //$NON-NLS-1$
		}
	}

	private void createListener(Observable observable, List<PropertyData> tail, Runnable runner) {
		links.add(new LinkedListener((Observable) observable, tail, runner));
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (data.isAnyProperty) {
			// no-op - we only need to notify
		} else if (data.isCollection) {
			handleChangedCollection(evt);
		} else if (Observable.class.isAssignableFrom(data.propertyType)) {
			handleChangedObservable(evt);
		} else {
			// no-op - we only need to notify
		}
		runner.run();
	}

	private void handleChangedCollection(PropertyChangeEvent evt) {
		Collection<Observable> oldValue = (Collection<Observable>) evt.getOldValue();
		Collection<Observable> newValue = (Collection<Observable>) evt.getNewValue();
		
		// release existing listeners
		if (oldValue!=null) {
			for (Observable observable : oldValue) {
				release(observable);
			}
		}
		
		// hook new listeners
		if (!tail.isEmpty()) {
			if (newValue!=null) {
				for (Observable observable : newValue) {
					createListener(observable, tail, runner);
				}
			}
		}
	}

	private void handleChangedObservable(PropertyChangeEvent evt) {
		Observable oldValue = (Observable) evt.getOldValue();
		Observable newValue = (Observable) evt.getNewValue();
		if (oldValue!=null) {
			release(oldValue);
		}
		if (newValue!=null && !tail.isEmpty()) {
			createListener(newValue, tail, runner);
		}
	}

	private void release() {
		release(this.observed);
	}

	private void release(Observable observed) {
		if (data.isAnyProperty) {
			observed.removePropertyChangeListener(this);
		} else {
			observed.removePropertyChangeListener(data.propertyName, this);
			for (LinkedListener c : links) {
				c.release();
			}
			links.clear();
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+"@"+observed+"=>"+links;
	}

	/** For testing */
	/* package */ List<LinkedListener> getChain() {
		List<LinkedListener> list = new ArrayList<LinkedListener>();
		for (LinkedListener l : links) {
			list.add(l);
			list.addAll(l.getChain());
		}
		return list;
	}
}