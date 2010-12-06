package org.socialmusicdiscovery.rcp.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.socialmusicdiscovery.rcp.Activator;

public class LoadDataSource extends AbstractHandler {
	public static final String COMMAND_ID = LoadDataSource.class.getName();	

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Activator.getDefault().getDataSource().reset();
		return null;
	}
}
