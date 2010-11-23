package org.socialmusicdiscovery.rcp.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.hibernate.cfg.NotYetImplementedException;
import org.socialmusicdiscovery.rcp.Activator;
import org.socialmusicdiscovery.rcp.prefs.PreferenceConstants;

public class LaunchLocalServerAction extends Action implements IWorkbenchAction  {
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

	public LaunchLocalServerAction() {
		super("&Launch server");
		setId(getClass().getName());
	}

	@Override
	public void dispose() {
		// no-op
	}

	@Override
	public void run() {
		Job job = new MyJob(getDB()); 
//        IWorkbench workbench = PlatformUI.getWorkbench();
//		workbench.getProgressService().showInDialog(workbench.getDisplay().getActiveShell(), job); 

		job.schedule();
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

}
