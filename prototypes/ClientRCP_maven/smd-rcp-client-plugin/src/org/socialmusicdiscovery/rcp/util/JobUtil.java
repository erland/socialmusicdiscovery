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

package org.socialmusicdiscovery.rcp.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

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
//		double started = System.currentTimeMillis();
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
			return handleError(e, dialogTitle);
		} catch (InterruptedException e) {
			return handleError(e, dialogTitle);
		}
		
//        double elapsed = System.currentTimeMillis()-started;
//		System.out.println("ImportJob.run(): "+((long)elapsed));
		return true;
	}

	private static boolean handleError(Exception e, String dialogTitle) {
		String message = e.getCause()==null ? e.getMessage() : e.getCause().getMessage();
		MessageDialog.openError(null, dialogTitle, message);
		return false;
	}
}
