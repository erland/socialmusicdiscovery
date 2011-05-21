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
import org.socialmusicdiscovery.rcp.content.ObservableContributor;
import org.socialmusicdiscovery.rcp.content.ObservableRecording;
import org.socialmusicdiscovery.rcp.content.ObservableRelease;
import org.socialmusicdiscovery.rcp.editors.artist.ArtistEditor;
import org.socialmusicdiscovery.rcp.editors.recording.RecordingEditor;
import org.socialmusicdiscovery.rcp.editors.release.ReleaseEditor;
import org.socialmusicdiscovery.rcp.error.FatalApplicationException;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.socialmusicdiscovery.server.business.model.core.Track;

public final class WorkbenchUtil {

	private final static Map<Class<?>, String> editors = new HashMap<Class<?>, String>();
	private final static Map<Class<?>, String> views = new HashMap<Class<?>, String>();

	static {
		editors.put(ObservableArtist.class, ArtistEditor.ID);
		editors.put(ObservableRelease.class, ReleaseEditor.ID);
		editors.put(ObservableRecording.class, RecordingEditor.ID);
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

	/**
	 * Convenience method.
	 * @return {@link IEditorPart} or <code>null</code>
	 */
	public static IEditorPart getActiveEditor() {
		IWorkbenchPage page = getActivePage();
		return page==null ? null : page.getActiveEditor();
	}

	/**
	 * Convenience method.
	 * @return {@link IWorkbenchPage} or <code>null</code>
	 */
	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow window = getWindow();
		IWorkbenchPage page = window==null ? null : window.getActivePage();
		return page;
	}

	/**
	 * Convenience method.
	 * @return {@link IWorkbenchWindow} or <code>null</code>
	 */
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

	public static void openDistinct(Object selectedElement) {
		IEditorPart activeEditor = getActiveEditor();
		IEditorInput currentInput= activeEditor==null ? null : activeEditor.getEditorInput();
		IEditorInput nextInput = resolveEditableElement(currentInput, selectedElement);
		String editorId = resolveEditorId(nextInput);
		String viewId = resolveViewId(nextInput);
		IWorkbenchPart part = null;
		
		if (editorId!=null) {
			part = WorkbenchUtil.openEditor(nextInput, editorId);
		} else if (viewId!=null) {
			part = WorkbenchUtil.openView(viewId, viewId);
		} else {
			NotYetImplemented.openDialog("Unable to locate an editor for " + nextInput);
		}
		if (part!=null) {
			
		}
	}

	private static IEditorInput resolveEditableElement(IEditorInput currentEditorInput, Object selectedElement) {
		if (selectedElement instanceof Track) {
			Track track = (Track) selectedElement;
			// what to open next depends on where we're currently editing the track
			// Recording => Release, Release => Recording
			Object target = currentEditorInput instanceof Recording ? track.getRelease() : track.getRecording();
			return resolveEditableElement(currentEditorInput, target);
		}
		if (selectedElement instanceof ObservableContributor) {
			ObservableContributor c = (ObservableContributor) selectedElement;
			// what to open next depends on where we're currently editing the contribution
			// Artist => Entity, anything else => Artist
			Object target = currentEditorInput instanceof Artist ? c.getOwner() : c.getArtist();
			return resolveEditableElement(currentEditorInput, target);
		}
		if (selectedElement instanceof Contributor) {
			return resolveEditableElement(currentEditorInput, ((Contributor) selectedElement).getArtist());
		}
		if (selectedElement instanceof IEditorInput) {
			return (IEditorInput) selectedElement;
		}
		if (selectedElement instanceof IAdaptable) {
			IAdaptable a = (IAdaptable) selectedElement;
			return resolveEditableElement(currentEditorInput, a.getAdapter(IEditorInput.class));
		}
		return null;
	}

	private static String resolveEditorId(Object element) {
		// TODO this will need to be made much more robust, we cannot depend on the base class
		// the editor should probably declare an interface that it can handle, perhaps 
		// thru a contentTypeId referring to the extensions of org.eclipse.core.contenttype.contentTypes
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

	public static Set<IEditorReference> findActiveEditorsInAnyWorkbenchWindow(Object... editorInputs) {
		try {
			Set<IEditorReference> result = new HashSet<IEditorReference>();
			for (IWorkbenchWindow w : PlatformUI.getWorkbench().getWorkbenchWindows()) {
				for (IWorkbenchPage p : w.getPages()) {
					for (IEditorReference ref : p.getEditorReferences()) {
						for (Object input : editorInputs) {
							if (input==ref.getEditorInput()) {
								result.add(ref);
							}
						}
					}
				}
			}
			return result;
		} catch (PartInitException e) {
			throw new FatalApplicationException("Could not find open editors for inputs: "+Arrays.asList(editorInputs), e);  //$NON-NLS-1$
		}
	}

	/**
	 * Close all open editors. Prompt to save changes if any editor has unsaved changes.
	 * @return <code>true</code> if all editors were closed, <code>false if not</code>
	 */
	public static boolean closeAllEditors() {
		IWorkbenchPage page = getActivePage();
		if (page != null) {
			page.closeAllEditors(true);
		}
		return page.getEditorReferences().length<1;
	}

	/**
	 * Convenience method that accepts a collection, primarily to avoid errors
	 * by passing a collection as the fiest varargs element to
	 * {@link #closeEditors(Object...)}.
	 * 
	 * @param victims
	 * @return boolean
	 * @see #closeEditors(Object...)
	 */
	public static boolean closeEditors(Collection<?> victims) {
		return closeEditors(victims.toArray(new Object[victims.size()]));
	}

	/**
	 * Close all open editors for supplied objects, if any. Prompt to save
	 * changes if any editor has unsaved changes.
	 * 
	 * @param victims
	 * @return <code>true</code> if all editors were closed, <code>false if not</code>
	 */
	public static boolean closeEditors(Object... victims) {
		Set<IEditorReference> refs = findActiveEditorsInAnyWorkbenchWindow(victims);
		IEditorReference[] array =  refs.toArray(new IEditorReference[refs.size()]);
		return getActivePage().closeEditors(array, true);
	}
}
