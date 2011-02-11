package org.socialmusicdiscovery.rcp.commands;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.socialmusicdiscovery.rcp.util.WorkbenchUtil;


public class OpenEntityEditor extends AbstractHandler implements IHandler {
	public static final String COMMAND_ID = OpenEntityEditor.class.getName();	

	@Override
	public Object execute(ExecutionEvent event ) throws ExecutionException {
		WorkbenchUtil.open(event);
		return null;
	}
}
