package org.socialmusicdiscovery.rcp.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.socialmusicdiscovery.rcp.editors.artist.ArtistEditor;
import org.socialmusicdiscovery.rcp.editors.release.ReleaseEditor;
import org.socialmusicdiscovery.rcp.error.FatalApplicationException;
import org.socialmusicdiscovery.rcp.views.util.SMDEditorInput;
import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Release;

public final class WorkbenchUtil {

	private final static Map<Class<?>, String> editors = new HashMap<Class<?>, String>();
	private final static Map<Class<?>, String> views = new HashMap<Class<?>, String>();

	static {
		editors.put(Artist.class, ArtistEditor.ID);
		editors.put(Release.class, ReleaseEditor.ID);
	}

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

	@SuppressWarnings("rawtypes")
	private static SMDEntity getSelectedEntity(OpenEvent event) {
		StructuredSelection selection = (StructuredSelection) event.getSelection();
		Object selected = selection.getFirstElement();
		return selected instanceof SMDEntity ? (SMDEntity) selected : null;
	}

	public static void open(OpenEvent event) {
		SMDEntity<?> entity = getSelectedEntity(event);
		openAll(entity);
	}

	public static void openAll(SMDEntity<?>... list) {
		for (SMDEntity<?> element : list) {
			SMDEntity<?> entity = resolveEditableElement(element);
			String editorId = resolveEditorId(entity);
			String viewId = resolveViewId(entity);
			
			if (editorId!=null) {
				WorkbenchUtil.openEditor(entity, editorId);
			} else {
				if (viewId!=null) {
					WorkbenchUtil.openView(viewId, entity.getId());
				}
			}
		}
	}

	private static SMDEntity<?> resolveEditableElement(SMDEntity<?> element) {
		if (element instanceof Contributor) {
			Contributor c = (Contributor) element;
			return c.getArtist();
		}
		return element;
	}

	private static String resolveEditorId(Object element) {
		// TODO this will need to be made much more robust, we cannot depend on the base class
		// the editor should probably declare an interface that it can handle 
		return editors.get(element.getClass());
	}

	private static String resolveViewId(Object element) {
		// TODO this will need to be made much more robust, we cannot depend on the base class
		// the view should probably declare an interface that it can handle 
		return views.get(element.getClass());
	}

}
