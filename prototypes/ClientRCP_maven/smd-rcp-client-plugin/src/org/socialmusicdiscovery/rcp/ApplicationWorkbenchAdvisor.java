package org.socialmusicdiscovery.rcp;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.statushandlers.AbstractStatusHandler;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.statushandlers.WorkbenchErrorHandler;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

    private class MyWorkbenchErrorHandler extends WorkbenchErrorHandler {

		@Override
		public void handle(StatusAdapter statusAdapter, int style) {
			// kludge, must be a way to tell the default handler to show error?
			super.handle(statusAdapter, style|StatusManager.SHOW);
		}

	}

	private AbstractStatusHandler workbenchErrorHandler;

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }
    
    public void initialize(IWorkbenchConfigurer configurer) {
        super.initialize(configurer);
        configurer.setSaveAndRestore(true);
    }

	public String getInitialWindowPerspectiveId() {
		return Perspective.ID;
	}
	
	public synchronized AbstractStatusHandler getWorkbenchErrorHandler() {
		if (workbenchErrorHandler == null) {
			workbenchErrorHandler = new MyWorkbenchErrorHandler();
		}
		return workbenchErrorHandler;
	}
}
