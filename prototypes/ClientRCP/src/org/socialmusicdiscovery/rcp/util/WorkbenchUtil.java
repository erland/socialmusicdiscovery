package org.socialmusicdiscovery.rcp.util;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.socialmusicdiscovery.rcp.error.FatalApplicationException;
import org.socialmusicdiscovery.rcp.views.util.SMDEditorInput;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

public final class WorkbenchUtil {

	private WorkbenchUtil() { } // static util

	public static void openView(String viewId, String secondaryId) {
		try {
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			page.showView(viewId, secondaryId, IWorkbenchPage.VIEW_ACTIVATE);
		} catch (PartInitException e) {
			throw new FatalApplicationException("Failed to open view: "+viewId+"/"+secondaryId);
		}
	}

	public static IEditorPart openEditor(SMDEntity<?> entity, String editorId) {
		SMDEditorInput input = new SMDEditorInput(entity);
		try {
			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			return page.openEditor(input, editorId, true);
		} catch (PartInitException e) {
			throw new FatalApplicationException("Failed to open editor: "+editorId+"/"+entity);
		}
	}

}
