package org.socialmusicdiscovery.rcp.views.navigator;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.socialmusicdiscovery.rcp.content.ModelObject;

class NavigatorListFactory implements IObservableFactory {

	@Override
	public IObservableList createObservable(Object target) {
		if (target instanceof ModelObject) {
			ModelObject model = (ModelObject) target;
			return model.getObservableChildren();
		}
		throw new IllegalArgumentException("Unknown type: "+target);
	}
}
