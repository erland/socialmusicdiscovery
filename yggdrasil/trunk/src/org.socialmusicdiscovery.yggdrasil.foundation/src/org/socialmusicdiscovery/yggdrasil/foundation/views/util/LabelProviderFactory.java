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

package org.socialmusicdiscovery.yggdrasil.foundation.views.util;

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
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ModelObject;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableEntity;

/**
 * A static factory for creating {@link LabelProvider}s for {@link Viewer}s.
 * 
 * @author Peer Törngren
 *
 */
public final class LabelProviderFactory  {

	private LabelProviderFactory() {}

	/**
	 * Use in static viewers and/or modal dialogs where properties are stable while viewer is visible.   
	 * @return {@link ILabelProvider}
	 */
	public static ILabelProvider forStaticList() {
		return new DefaultLabelProvider();
	}

	/**
	 * Use in dynamic tree viewers where properties may change as a result of other activity.   
	 * @param contentProvider
	 * @return {@link ObservableMapLabelProvider}
	 */
	public static ObservableMapLabelProvider forNavigator(ObservableListTreeContentProvider contentProvider) {
		return new ObservableEntityColumnLabelProvider(createObservableAttributes(contentProvider.getKnownElements(), ObservableEntity.PROP_name, ObservableEntity.PROP_dirty));
	}
	
	private static IObservableMap[] createObservableAttributes(IObservableSet setToObserve, String... propertyNames) {
		IValueProperty[] propertiesToObserve = BeanProperties.values(propertyNames);
		return Properties.observeEach(setToObserve, propertiesToObserve);
	}

	/**
	 * Return a standard label provider for any kind of {@link ModelObject} that
	 * only need its name and/or image presented in the column. Caller must
	 * supply the simple (NOT nested) property name that will return an instance
	 * of {@link ModelObject}.The returned Label provider is expected to be used
	 * as a delegate by a {@link DelegatingObservableMapLabelProvider}.
	 * 
	 * @param modelObjectPropertyName
	 * @return {@link ModelObjectColumnLabelProvider}
	 */
	public static ModelObjectColumnLabelProvider newModelObjectDelegate(String modelObjectPropertyName) {
		return new ModelObjectColumnLabelProvider(modelObjectPropertyName);
	}
	
	/**
	 * Return a standard label provider for any kind of {@link ModelObject} that
	 * only need its name and/or image presented in the column. The returned 
	 * label provider is expected to be used as a delegate by a 
	 * {@link DelegatingObservableMapLabelProvider}.
	 * 
	 * @return {@link ModelObjectColumnLabelProvider}
	 */
	public static ModelObjectColumnLabelProvider newModelObjectDelegate() {
		return new ModelObjectColumnLabelProvider();
	}
	/**
	 * Return a standard label provider for any kind of {@link ObservableEntity}
	 * that needs its type name and/or image presented in the column. Caller
	 * must supply the simple (NOT nested) property name that will return an
	 * instance of {@link ObservableEntity}. The returned Label provider is
	 * expected to be used as a delegate by a
	 * {@link DelegatingObservableMapLabelProvider}.
	 * 
	 * @param entityPropertyName
	 * @return {@link EntityTypeColumnLabelProvider}
	 */
	public static EntityTypeColumnLabelProvider newEntityTypeDelegate(String entityPropertyName) {
		// TODO Auto-generated method stub
		return new EntityTypeColumnLabelProvider(entityPropertyName);
	}
	
	/**
	 * Return a standard label provider to present the
	 * {@link Contributor#getType()} property in a grid. The returned Label
	 * provider is expected to be used as a delegate by a
	 * {@link DelegatingObservableMapLabelProvider}.
	 * 
	 * @return {@link ContributorTypeColumnLabelProvider}
	 */
	public static ContributorTypeColumnLabelProvider newContributorTypeDelegate() {
		return new ContributorTypeColumnLabelProvider();
	}

	/**
	 * Return a standard label provider for a regular {@link String} property
	 * that can be readily presented as is. Caller must supply the simple (NOT
	 * nested) property name that will return an {@link Integer}. The returned
	 * Label provider is expected to be used as a delegate by a
	 * {@link DelegatingObservableMapLabelProvider}.
	 * 
	 * @param stringPropertyName
	 * @return {@link StringColumnLabelProvider}
	 */
	public static StringColumnLabelProvider newStringDelegate(String stringPropertyName) {
		return new StringColumnLabelProvider(stringPropertyName);
	}

	/**
	 * Return a standard label provider for a regular {@link Integer} property
	 * that can be readily presented as is. Caller must supply the simple (NOT
	 * nested) property name that will return a {@link String}. The returned
	 * Label provider is expected to be used as a delegate by a
	 * {@link DelegatingObservableMapLabelProvider}.
	 * 
	 * @param numberPropertyName
	 * @return {@link IntegerColumnLabelProvider}
	 */
	public static IntegerColumnLabelProvider newIntegerDelegate(String numberPropertyName) {
		return new IntegerColumnLabelProvider(numberPropertyName);
	}

}
