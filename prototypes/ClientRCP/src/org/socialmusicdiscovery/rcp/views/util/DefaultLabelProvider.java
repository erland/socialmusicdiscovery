package org.socialmusicdiscovery.rcp.views.util;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.socialmusicdiscovery.rcp.content.ModelObject;

public class DefaultLabelProvider extends CellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		ModelObject m = (ModelObject) cell.getElement();
		cell.setText(m.getName());
	}

}
