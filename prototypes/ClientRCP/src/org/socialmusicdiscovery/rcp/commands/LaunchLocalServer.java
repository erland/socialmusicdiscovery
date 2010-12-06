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
import org.socialmusicdiscovery.rcp.error.NotYetImplementedException;
import org.socialmusicdiscovery.rcp.prefs.PreferenceConstants;

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

		@SuppressWarnings("unused")
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
					throw new NotYetImplementedException("Cannot yet run local server");
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
