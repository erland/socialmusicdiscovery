package org.socialmusicdiscovery.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.socialmusicdiscovery.rcp.Activator;

public class DataSourceLoadAction extends Action implements IWorkbenchAction  {
	public DataSourceLoadAction() {
		super("&Reload");
		setId(getClass().getName());
	}

	@Override
	public void dispose() {
		// no-op
	}

	@Override
	public void run() {
		Activator.getDefault().getDataSource().reset();
	}
}
