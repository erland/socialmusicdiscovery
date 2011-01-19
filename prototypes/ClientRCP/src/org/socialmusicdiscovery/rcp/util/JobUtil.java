package org.socialmusicdiscovery.rcp.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.socialmusicdiscovery.rcp.error.FatalApplicationException;

/**
 * Some helpers for long-running operations of various kinds (not necessarily {@link Job}s.
 * 
 * @author Peer Törngren
 *
 */
public class JobUtil {

	/** A simple runner. Should/could be extended to run in background etc.
	 * @param shell
	 * @param runnable
	 * @param dialogTitle
	 * @return <code>true</code> if job finished ok, <code>false</code> if not (e.g. user cancelled)
	 */
	public static boolean run(Shell shell, IRunnableWithProgress runnable, String dialogTitle) {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.create();
			dialog.getShell().setText(dialogTitle);
			dialog.run(true, true, runnable);
			if (dialog.getProgressMonitor().isCanceled()) {
				MessageDialog.openInformation(shell, dialogTitle+" cancelled", "Operation was cancelled");
				return false;
			}
		} catch (InvocationTargetException e) {
			throw new FatalApplicationException(dialogTitle, e);
		} catch (InterruptedException e) {
			throw new FatalApplicationException(dialogTitle, e);
		}
		return true;
	}
}
