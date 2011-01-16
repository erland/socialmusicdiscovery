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

	public static void run(Shell shell, IRunnableWithProgress runnable, String dialogTitle) {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.create();
			dialog.getShell().setText(dialogTitle);
			dialog.run(true, false, runnable);
			if (dialog.getProgressMonitor().isCanceled()) {
				MessageDialog.openInformation(shell, dialogTitle+" cancelled", "Operation was cancelled");
			}
		} catch (InvocationTargetException e) {
			throw new FatalApplicationException(dialogTitle, e);
		} catch (InterruptedException e) {
			throw new FatalApplicationException(dialogTitle, e);
		}
	}
}
