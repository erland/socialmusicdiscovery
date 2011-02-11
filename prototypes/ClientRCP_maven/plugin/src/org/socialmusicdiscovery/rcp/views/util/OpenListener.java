package org.socialmusicdiscovery.rcp.views.util;


import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.socialmusicdiscovery.rcp.util.ViewerUtil;

public class OpenListener implements IOpenListener {
	@Override
	public void open(OpenEvent event) {
		ViewerUtil.handleOpen(event);
	}
}
