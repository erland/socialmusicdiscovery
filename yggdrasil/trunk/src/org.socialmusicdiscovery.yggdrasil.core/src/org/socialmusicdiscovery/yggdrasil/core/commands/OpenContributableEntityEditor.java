package org.socialmusicdiscovery.yggdrasil.core.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.yggdrasil.foundation.util.ViewerUtil;
import org.socialmusicdiscovery.yggdrasil.foundation.util.WorkbenchUtil;

public class OpenContributableEntityEditor extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		Object[] selected = ViewerUtil.getSelectedObjects(selection);
		for (Object s : selected) {
			if (s instanceof Contributor) {
				Contributor t = (Contributor) s;
				WorkbenchUtil.openDistinct(t.getOwner());
			}
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO return false if no editor is registered
		return super.isEnabled();
	}
}
