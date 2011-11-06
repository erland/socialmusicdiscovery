package org.socialmusicdiscovery.yggdrasil.core.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.yggdrasil.foundation.util.ViewerUtil;
import org.socialmusicdiscovery.yggdrasil.foundation.util.WorkbenchUtil;

public class OpenTrackReleaseEditor extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event ) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		Object[] selected = ViewerUtil.getSelectedObjects(selection);
		for (Object s : selected) {
			if (s instanceof Track) {
				Track t = (Track) s;
				WorkbenchUtil.openDistinct(t.getRelease());
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
