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

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;
import org.socialmusicdiscovery.yggdrasil.foundation.event.Observable;

/**
 * Listen to changes in observable instances and fires a
 * {@link PropertyChangeEvent} whenever the observed instance changes. Use to
 * "transform" data binding change events into traditional "bean-style" events,
 * primarily intended to observe {@link IObservableCollection}s.
 * 
 * @author Peer Törngren
 * 
 */
public class ChangeReplicator implements ISetChangeListener, IListChangeListener, IMapChangeListener, IChangeListener {

		private final Observable observable;
		private final String[] propertyNames;

		/** Private - use static factory methods to instantiate. */
		private ChangeReplicator(Observable observable, String... propertyNames) {
			this.observable = observable;
			this.propertyNames = propertyNames;
		}

		@Override
		public void handleSetChange(SetChangeEvent event) {
			firePropertyChangeEvent();
		}

		@Override
		public void handleMapChange(MapChangeEvent event) {
			firePropertyChangeEvent();
		}

		@Override
		public void handleListChange(ListChangeEvent event) {
			firePropertyChangeEvent();
		}

		@Override
		public void handleChange(ChangeEvent event) {
			firePropertyChangeEvent();
		}

		private void firePropertyChangeEvent() {
			for (String propertyName : propertyNames) {
				observable.firePropertyChange(propertyName);
			}
		}

	/**
	 * Listen to changes in observable <code>source</code> and fire a
	 * {@link PropertyChangeEvent} with supplied property name(s) on supplied
	 * <code>target</code> whenever the observed instance changes.
	 * 
	 * @param source
	 * @param target
	 * @param propertyNames
	 */
	public static void replicate(IObservableSet source, Observable target, String... propertyNames) {
		source.addSetChangeListener(new ChangeReplicator(target, propertyNames));
	}

	/**
	 * Listen to changes in observable <code>source</code> and fire a
	 * {@link PropertyChangeEvent} with supplied property name(s) on supplied
	 * <code>target</code> whenever the observed instance changes.
	 * 
	 * @param source
	 * @param target
	 * @param propertyNames
	 */
	public static void replicate(IObservableList source, Observable target, String... propertyNames) {
		source.addListChangeListener(new ChangeReplicator(target, propertyNames));
	}

	/**
	 * Listen to changes in observable <code>source</code> and fire a
	 * {@link PropertyChangeEvent} with supplied property name(s) on supplied
	 * <code>target</code> whenever the observed instance changes.
	 * 
	 * @param source
	 * @param target
	 * @param propertyNames
	 */
	public static void replicate(IObservableMap source, Observable target, String... propertyNames) {
		source.addMapChangeListener(new ChangeReplicator(target, propertyNames));
	}

	/**
	 * Listen to changes in observable <code>source</code> and fire a
	 * {@link PropertyChangeEvent} with supplied property name(s) on supplied
	 * <code>target</code> whenever the observed instance changes.
	 * 
	 * @param source
	 * @param target
	 * @param propertyNames
	 */
	public static void replicate(IObservable source, Observable target, String... propertyNames) {
		source.addChangeListener(new ChangeReplicator(target, propertyNames));
	}

}
