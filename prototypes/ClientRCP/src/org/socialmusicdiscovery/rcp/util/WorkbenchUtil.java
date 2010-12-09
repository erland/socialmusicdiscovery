package org.socialmusicdiscovery.rcp.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.services.IEvaluationService;
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

	public static IViewPart openView(String viewId, String secondaryId) {
		try {
			IWorkbenchPage page = getActivePage();
			return page.showView(viewId, secondaryId, IWorkbenchPage.VIEW_ACTIVATE);
		} catch (PartInitException e) {
			throw new FatalApplicationException("Failed to open view: "+viewId+"/"+secondaryId);
		}
	}

	public static IEditorPart openEditor(SMDEntity<?> entity, String editorId) {
		SMDEditorInput input = new SMDEditorInput(entity);
		try {
			IWorkbenchPage page = getActivePage();
			return page.openEditor(input, editorId, true);
		} catch (PartInitException e) {
			throw new FatalApplicationException("Failed to open editor: "+editorId+"/"+entity);
		}
	}

	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow window = getWindow();
		IWorkbenchPage page = window.getActivePage();
		return page;
	}

	public static IWorkbenchWindow getWindow() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		return window;
	}

	 @SuppressWarnings("unchecked")
	public static <T> T getService(Class<?> api) {
		return (T) getWindow().getService(api);
	}
	 
	public static IHandlerService getHandlerService() {
		return getService(IHandlerService.class);
	}

	public static IEvaluationService getEvaluationService() {
		return getService(IEvaluationService.class);
	}
	
	@SuppressWarnings("rawtypes")
	private static SMDEntity getSelectedEntity(OpenEvent event) {
		StructuredSelection selection = (StructuredSelection) event.getSelection();
		Object selected = selection.getFirstElement();
		return selected instanceof SMDEntity ? (SMDEntity) selected : null;
	}

	public static void open(OpenEvent event) {
		SMDEntity<?> entity = getSelectedEntity(event);
		openDistinct(entity);
	}

	public static void openAll(Object... elements) {
		for (Object element : elements) {
			if (element instanceof Collection) {
				Collection<?> c = (Collection<?>) element;
				openAll(c.toArray());
			} else if (element !=null) {
				openDistinct(element);
			} else {
				throw new IllegalArgumentException("Found null element: " + Arrays.asList(elements));
			}
		}
	}

	public static void openDistinct(Object element) {
		SMDEntity<?> entity = resolveEditableElement(element);
		String editorId = resolveEditorId(entity);
		String viewId = resolveViewId(entity);
		IWorkbenchPart part = null;
		
		if (editorId!=null) {
			part = WorkbenchUtil.openEditor(entity, editorId);
		} else if (viewId!=null) {
			part = WorkbenchUtil.openView(viewId, entity.getId());
		}
		if (part!=null) {
			
		}
	}

	private static SMDEntity<?> resolveEditableElement(Object element) {
		if (element instanceof Contributor) {
			Contributor c = (Contributor) element;
			return c.getArtist();
		}
		return element instanceof SMDEntity ? (SMDEntity<?>) element : null;
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

	public static Object open(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		Object[] selected = ViewerUtil.getSelectedEntities(selection);
		WorkbenchUtil.openAll(selected);
		return null;
	}

}
