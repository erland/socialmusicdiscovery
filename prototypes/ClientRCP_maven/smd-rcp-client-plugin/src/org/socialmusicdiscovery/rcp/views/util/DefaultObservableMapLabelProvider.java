package org.socialmusicdiscovery.rcp.views.util;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.socialmusicdiscovery.rcp.content.ObservableEntity;

public class DefaultObservableMapLabelProvider extends ObservableMapLabelProvider {

	public DefaultObservableMapLabelProvider(IObservableMap[] observableAttributes) {
		super(observableAttributes);
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return isEntityNameColumn(element, columnIndex) ? getEntityImage((ObservableEntity) element) : super.getColumnImage(element, columnIndex);
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		return isEntityNameColumn(element, columnIndex) ? getEntityName((ObservableEntity) element) : super.getColumnText(element, columnIndex);
	}

	private boolean isEntityNameColumn(Object element, int columnIndex) {
		return columnIndex==0 && element instanceof ObservableEntity;
	}

	private String getEntityName(ObservableEntity entity) {
		// TODO better rendering, perhaps using color, font, image and/or image decorator? 
		return entity.isDirty() ? entity.getName()+" *" : entity.getName();
	}

	private Image getEntityImage(ObservableEntity entity) {
		// TODO images for entities
		return null;
	}

}
