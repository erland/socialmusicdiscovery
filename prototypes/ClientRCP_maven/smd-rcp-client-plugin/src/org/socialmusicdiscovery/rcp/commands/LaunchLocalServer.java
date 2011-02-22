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

package org.socialmusicdiscovery.rcp.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.socialmusicdiscovery.rcp.Activator;
import org.socialmusicdiscovery.rcp.prefs.PreferenceConstants;
import org.socialmusicdiscovery.rcp.util.NotYetImplemented;

public class LaunchLocalServer extends AbstractHandler  {
	public static final String COMMAND_ID = LaunchLocalServer.class.getName();	
	private class MyJob extends Job {

		private static final String DB_KEY = "org.socialmusicdiscovery.server.database";
		private final String db;

		public MyJob(String db) {
			super("Local server");
//			setUser(true);
//			setSystem(false);
			this.db = db;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			String name = "Running default server";
			try {
				if (db!=null && db.length()>0) {
					System.setProperty(DB_KEY, db);
					name = "Running with db args: " + db;
				}
				monitor.beginTask(name, IProgressMonitor.UNKNOWN);
				if (true) {
					NotYetImplemented.openDialog("Cannot yet run local server");
				}
//				SMDApplication app = new SMDApplication();
				while(!monitor.isCanceled()) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}
				}
				monitor.done();
				return new Status(IStatus.OK, Activator.PLUGIN_ID, db);
			} catch (Exception e) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, db, e);
			}
		}
	}


//	private String getCommand() {
//		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
//		String file = store.getString(PreferenceConstants.P_LOCALSERVER_FILENAME);
//		String cmdPattern = store.getString(PreferenceConstants.P_LOCALSERVER_COMMAND);
//		return MessageFormat.format(cmdPattern, file);
//	}

	private String getDB() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String value = store.getString(PreferenceConstants.P_LOCALSERVER_DB);
		return value;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Job job = new MyJob(getDB()); 
//      IWorkbench workbench = PlatformUI.getWorkbench();
//		workbench.getProgressService().showInDialog(workbench.getDisplay().getActiveShell(), job); 

		job.schedule();
		return null;
	}

}
