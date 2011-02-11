package org.socialmusicdiscovery.rcp.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
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
import org.socialmusicdiscovery.rcp.content.ObservableArtist;
import org.socialmusicdiscovery.rcp.content.ObservableRelease;
import org.socialmusicdiscovery.rcp.editors.artist.ArtistEditor;
import org.socialmusicdiscovery.rcp.editors.release.ReleaseEditor;
import org.socialmusicdiscovery.rcp.error.FatalApplicationException;
import org.socialmusicdiscovery.server.business.model.core.Contributor;

public final class WorkbenchUtil {

	private final static Map<Class<?>, String> editors = new HashMap<Class<?>, String>();
	private final static Map<Class<?>, String> views = new HashMap<Class<?>, String>();

	static {
		editors.put(ObservableArtist.class, ArtistEditor.ID);
		editors.put(ObservableRelease.class, ReleaseEditor.ID);
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

	public static IEditorPart openEditor(IEditorInput input, String editorId) {
		try {
			IWorkbenchPage page = getActivePage();
			return page.openEditor(input, editorId, true);
		} catch (PartInitException e) {
			throw new FatalApplicationException("Failed to open editor: "+editorId+"/"+input);
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
		IEditorInput input = resolveEditableElement(element);
		String editorId = resolveEditorId(input);
		String viewId = resolveViewId(input);
		IWorkbenchPart part = null;
		
		if (editorId!=null) {
			part = WorkbenchUtil.openEditor(input, editorId);
		} else if (viewId!=null) {
			part = WorkbenchUtil.openView(viewId, viewId);
		}
		if (part!=null) {
			
		}
	}

	private static IEditorInput resolveEditableElement(Object element) {
		if (element instanceof Contributor) {
			Contributor c = (Contributor) element;
			return resolveEditableElement(c.getArtist());
		}
		if (element instanceof IEditorInput) {
			return (IEditorInput) element;
		}
		if (element instanceof IAdaptable) {
			IAdaptable a = (IAdaptable) element;
			return resolveEditableElement(a.getAdapter(IEditorInput.class));
		}
		return null;
	}

	private static String resolveEditorId(Object element) {
		// TODO this will need to be made much more robust, we cannot depend on the base class
		// the editor should probably declare an interface that it can handle 
		return element==null ? null : editors.get(element.getClass());
	}

	private static String resolveViewId(Object element) {
		// TODO this will need to be made much more robust, we cannot depend on the base class
		// the view should probably declare an interface that it can handle 
		return element==null ? null : views.get(element.getClass());
	}

	public static Object open(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		Object[] selected = ViewerUtil.getSelectedObjects(selection);
		openAll(selected);
		return null;
	}

	public static Set<IEditorReference> findActiveEditorsInAnyWorkbenchWindow(IEditorInput editorInput) {
		try {
			Set<IEditorReference> result = new HashSet<IEditorReference>();
			for (IWorkbenchWindow w : PlatformUI.getWorkbench().getWorkbenchWindows()) {
				for (IWorkbenchPage p : w.getPages()) {
					for (IEditorReference ref : p.getEditorReferences()) {
						if (editorInput==ref.getEditorInput()) {
							result.add(ref);
						}
					}
				}
			}
			return result;
		} catch (PartInitException e) {
			throw new FatalApplicationException("Could not find open editors for input: "+editorInput, e);  //$NON-NLS-1$
		}
	}

}
