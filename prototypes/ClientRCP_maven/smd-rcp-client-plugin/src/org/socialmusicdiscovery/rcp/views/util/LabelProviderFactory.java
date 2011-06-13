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

package org.socialmusicdiscovery.rcp.views.util;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.socialmusicdiscovery.rcp.content.ObservableEntity;

/**
 * A static factory for creating {@link LabelProvider}s for {@link Viewer}s.
 * 
 * @author Peer Törngren
 *
 */
public final class LabelProviderFactory  {

	private LabelProviderFactory() {}

	
	/**
	 * Use in dynamic viewers where properties may change as a result of other activity.   
	 * @param contentProvider
	 * @return {@link ObservableMapLabelProvider}
	 */
	public static ObservableMapLabelProvider defaultObservable(ObservableListTreeContentProvider contentProvider) {
		return new DefaultObservableMapLabelProvider(createObservableAttributes(contentProvider, ObservableEntity.PROP_name, ObservableEntity.PROP_dirty));
	}
	
	private static IObservableMap[] createObservableAttributes(ObservableListTreeContentProvider contentProvider, String... propertyNames) {
		IObservableSet listToObserve = contentProvider.getKnownElements();
		IValueProperty[] propertiesToObserve = BeanProperties.values(propertyNames);
		return Properties.observeEach(listToObserve, propertiesToObserve);
	}


	/**
	 * Use in static viewers and/or modal dialogs where properties are stable while viewer is visible.   
	 * @return {@link ILabelProvider}
	 */
	public static ILabelProvider defaultStatic() {
		return new DefaultLabelProvider();
	}


}
