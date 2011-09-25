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

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang.time.StopWatch;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Some helpers for long-running operations of various kinds (not necessarily {@link Job}s.
 * 
 * @author Peer Törngren
 *
 */
public final class JobUtil {

	private JobUtil() {}

	private static class MyJobListener extends JobChangeAdapter implements Runnable {

		private final StopWatch sw = new StopWatch();
		private IJobChangeEvent event;
		
		@Override
		public void aboutToRun(IJobChangeEvent event) {
			sw.start();
		}

		@Override
		public void done(IJobChangeEvent event) {
			this.event = event;
			Display.getDefault().syncExec(this);
		}

		@Override
		public void run() {
			sw.split();
			String time = sw.toSplitString();
				
			IStatus result = event.getResult();
			String dlgTitle = event.getJob().getName();
			int severity = result.getSeverity();
			
			if (result.isOK()) {
				MessageDialog.openInformation(null, dlgTitle, "Done after "+time);
			} else if (severity==IStatus.CANCEL) {
				MessageDialog.openWarning(null, dlgTitle, "Canceled after "+time);
			} else if (severity==IStatus.WARNING) {
				MessageDialog.openWarning(null, dlgTitle, "Done with warnings after "+time+": "+result.getMessage());			
			} else if (severity==IStatus.ERROR) {
				MessageDialog.openError(null, dlgTitle, "Failed after "+time+": "+result.getMessage());			
			} else {
				throw new IllegalArgumentException("Unexpected status: "+result);
			}
		}
	}

	/**
	 * Schedule a job and attach a listener to report results back to user.
	 * TODO accept a listener to be notified about IStatus when job completes 
	 * @param job
	 */
	public static void schedule(Job job) {
		job.addJobChangeListener(new MyJobListener());
		job.schedule();
	}
	
	/**
	 * Convenience method, runs task in active shell.
	 * @param runnable
	 * @param dialogTitle
	 * @param isCancelable
	 * @return boolean
	 * @see #run(Shell, IRunnableWithProgress, String, boolean)
	 */
	public static boolean run(IRunnableWithProgress runnable, String dialogTitle, boolean isCancelable) {
		Shell shell = Display.getCurrent().getActiveShell();
		return run(shell, runnable, dialogTitle, isCancelable);
	}
	
	/**
	 * A simple runner with a progress monitor. For more sophisticated
	 * operation, consider running a {@link Job} thru {@link #schedule(Job)}. 
	 * 
	 * @param shell
	 * @param runnable
	 * @param dialogTitle
	 * @param isCancelable show Cancel button or not?
	 * @return <code>true</code> if job finished ok, <code>false</code> if not (e.g. user canceled)
	 * 
	 * @see #schedule(Job)
	 */
	public static boolean run(Shell shell, IRunnableWithProgress runnable, String dialogTitle, boolean isCancelable) {
//		double started = System.currentTimeMillis();
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);

		try {
			dialog.create();
			dialog.getShell().setText(dialogTitle);
			dialog.run(true, isCancelable, runnable);
			if (dialog.getProgressMonitor().isCanceled()) {
				MessageDialog.openInformation(shell, dialogTitle+" cancelled", "Operation was canceled");
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

	private static boolean handleError(Throwable e, String dialogTitle) {
		String message = e.getCause()==null ? e.getMessage() : e.getCause().getMessage();
		MessageDialog.openError(null, dialogTitle, message);
		return false;
	}

}
