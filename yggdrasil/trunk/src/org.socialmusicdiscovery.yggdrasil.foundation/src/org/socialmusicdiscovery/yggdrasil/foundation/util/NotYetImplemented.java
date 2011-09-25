/*
 *  Copyright 2010-2011, Social Music Discovery project
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of Social Music Discovery project nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.socialmusicdiscovery.yggdrasil.foundation.util;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Ask user to confirm before running unstable and potentially disastrous operations.
 * 
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

	public static boolean confirm(String op) {
		if (Display.getCurrent()==null) {
			return true; // avoid dialog if running unit tests
		}
		String title = "<NOT YET IMPLEMENTED>";
		String msg = op + " is not yet fully implemented and/or tested. Running this operation may result in a corrupt database. Proceed only if you are testing/developing this function." +
				"\n\nRECOMMENDATION: DO NOT PROCEED!" +
				"\n\nDo you want to proceed despite the risk of destroying your database?";

		String[] buttons = { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL };
		MessageDialog dialog = new MessageDialog(null, title, null, msg, MessageDialog.WARNING, buttons, 1);
		return dialog.open() == 0;
	}
}
