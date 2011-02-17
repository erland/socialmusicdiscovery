package org.socialmusicdiscovery.rcp.error;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.socialmusicdiscovery.rcp.Activator;

/**
 * An {@link ErrorDialog} with options to Retry, Ignore or Cancel operation that
 * caused the error.
 * 
 * TODO option to show stack trace?
 * TODO show MultiStatus for all nested "cause" exceptions? 
 * 
 * @author Peer Törngren
 * 
 */
public class ExtendedErrorDialog extends ErrorDialog {

	public static final int IGNORE_BUTTON = IDialogConstants.IGNORE_ID;
	public static final int RETRY_BUTTON = IDialogConstants.RETRY_ID;
	public static final int CANCEL_BUTTON = IDialogConstants.CANCEL_ID;

	private static final int MASK = IStatus.OK | IStatus.INFO | IStatus.WARNING | IStatus.ERROR;
	
	public ExtendedErrorDialog(Shell parentShell, String taskName, String problemSummary, String reason, Throwable e) {
		super(parentShell, taskName, problemSummary, createStatus(reason, e), MASK);
	}

	private static IStatus createStatus(String message, Throwable e) {
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.IGNORE_ID == buttonId) {
			setReturnCode(IGNORE_BUTTON);
			close();
		} else if (IDialogConstants.RETRY_ID == buttonId) {
			setReturnCode(RETRY_BUTTON);
			close();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			setReturnCode(CANCEL_BUTTON);
			close();
		} else {
			super.buttonPressed(buttonId);
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.RETRY_ID, IDialogConstants.RETRY_LABEL, false);
		createButton(parent, IDialogConstants.IGNORE_ID, IDialogConstants.IGNORE_LABEL, false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
		createDetailsButton(parent);
	}

}
