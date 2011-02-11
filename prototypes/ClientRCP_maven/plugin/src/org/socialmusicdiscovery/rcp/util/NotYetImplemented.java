package org.socialmusicdiscovery.rcp.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Make it easy to explain why things don't work yet.
 * @author Peer Törngren
 *
 */
public class NotYetImplemented {

	private NotYetImplemented() {}

	public static  void openDialog(String msg) {
		MessageDialog.openWarning(null, "Not Yet Implemented", msg);
	}

	public static  void openDialog(Shell shell, String msg) {
		MessageDialog.openWarning(shell, "Not Yet Implemented", msg);
	}
}
