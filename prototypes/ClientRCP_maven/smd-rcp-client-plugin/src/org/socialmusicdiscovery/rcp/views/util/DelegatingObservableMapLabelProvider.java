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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * An observable label provider that monitors a set of properties (possibly
 * nested), and makes use of internal delegates to do the actual rendering when
 * the observed properties change. The delegates are kept in an array that
 * matches the column indices; the delegates must be fed to the constructor in
 * tghe orde of the columns that they are expected to render.
 * 
 * @author Peer Törngren
 * 
 */
public class DelegatingObservableMapLabelProvider extends ObservableMapLabelProvider {

	private final ColumnLabelProvider[] delegates;  // in column order!

	/**
	 * Constructor.
	 * @param knownElements
	 * @param providers in column order! 
	 */
	public DelegatingObservableMapLabelProvider(IObservableSet knownElements, AbstractColumnLabelProviderDelegate... providers) {
		super(createAttributeMap(knownElements, providers));
		this.delegates = providers;
	}

	@SuppressWarnings("unchecked") // stupid warning, why? Should perhaps change compiler settings
	private static IObservableMap[] createAttributeMap(IObservableSet knownElements, AbstractColumnLabelProviderDelegate... baseProviders) {
		List<String> propertyNamesToObserve = new ArrayList<String>();
		for (AbstractColumnLabelProviderDelegate baseProvider : baseProviders) {
			propertyNamesToObserve.addAll(baseProvider.getPropertyNamesToObserve());
		}
		
		IValueProperty[] labelProperties = new IValueProperty[propertyNamesToObserve.size()];
		for (int i = 0; i < labelProperties.length; i++) {
			IBeanValueProperty property = BeanProperties.value(propertyNamesToObserve.get(i));
			labelProperties[i] = property;
		}
		
		return Properties.observeEach(knownElements, labelProperties);
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return delegates[columnIndex].getImage(element);
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		String text = delegates[columnIndex].getText(element);
		return text==null ? getNullString() : text;
//		String providerText = delegate.getColumnText(element, columnIndex);
//		return providerText == null ? getDefaultText(element, columnIndex) : providerText;
	}

//	private String getDefaultText(Object element, int columnIndex) {
//		Object value = attributeMaps[columnIndex].get(element);
//		return value == null ? "" : value.toString(); //$NON-NLS-1$
//	}

	/**
	 * Get a string to represent attributes that are not expected to be missing.
	 * In some odd case, subclasses may want to override.
	 * @return String
	 */
	protected String getNullString() {
		return "<?>";
	}

}
