package org.socialmusicdiscovery.rcp.views.util;

import javassist.tools.web.Viewer;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.socialmusicdiscovery.rcp.content.ObservableEntity;

/**
 * A static factory for creating {@link LabelProvider}s for {@link Viewer}s.
 * 
 * @author Peer Törngren
 *
 */
public final class LabelProviderFactory  {

	private LabelProviderFactory() {}

	
	public static ObservableMapLabelProvider defaultObservable(ObservableListTreeContentProvider contentProvider) {
		return new DefaultObservableMapLabelProvider(createObservableAttributes(contentProvider, ObservableEntity.PROP_name, ObservableEntity.PROP_dirty));
	}
	
	private static IObservableMap[] createObservableAttributes(ObservableListTreeContentProvider contentProvider, String... propertyNames) {
		IObservableSet listToObserve = contentProvider.getKnownElements();
		IValueProperty[] propertiesToObserve = BeanProperties.values(propertyNames);
		return Properties.observeEach(listToObserve, propertiesToObserve);
	}


}
