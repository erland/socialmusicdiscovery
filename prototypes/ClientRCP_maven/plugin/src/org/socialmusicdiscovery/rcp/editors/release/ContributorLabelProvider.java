package org.socialmusicdiscovery.rcp.editors.release;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.socialmusicdiscovery.server.business.model.core.Contributor;

public class ContributorLabelProvider extends CellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		Contributor c = (Contributor) cell.getElement();
		String text = c==null || c.getArtist()==null ? "" : c.getArtist().getName();
		cell.setText(text);
	}

}
