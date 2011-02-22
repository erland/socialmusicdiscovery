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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.services.IEvaluationService;

public class ViewerUtil {

	/**
	 * Not quite sure why, but it seems we need to monitor selection changes
	 * in internal viewers - the evaluation service is only updated when active part changes?
	 */
	private static final class MyBridgeFromSelectionProviderToEvaluationService extends AbstractSourceProvider implements ISelectionChangedListener {
		private static final String PROP = ISources.ACTIVE_CURRENT_SELECTION_NAME;
		private final Map<String, Object> map = new HashMap<String, Object>();

		private MyBridgeFromSelectionProviderToEvaluationService() {
			super();
			setCurrentSelection(null);
			IEvaluationService e = WorkbenchUtil.getEvaluationService();
			e.addSourceProvider(this);
		}

//		Does not work? It should, if I read the javadoc right
//		@Override
//		public void initialize(IServiceLocator locator) {
//			super.initialize(locator);
//			setCurrentSelection(null);
//			IEvaluationService e = (IEvaluationService) locator.getService(IEvaluationService.class);
//			e.addSourceProvider(this);
//		}

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			ISelection selection = event.getSelection();
			setCurrentSelection(selection);
			fireSourceChanged(1, PROP, selection);
		}

		private void setCurrentSelection(ISelection selection) {
			map.put(PROP, selection);
		}
		
		@Override
		public void dispose() {
			WorkbenchUtil.getEvaluationService().removeSourceProvider(this);
		}

		@Override
		public Map<String, Object> getCurrentState() {
			return Collections.unmodifiableMap(map);
		}

		@Override
		public String[] getProvidedSourceNames() {
			Set<String> keys = map.keySet();
			return (String[]) keys.toArray(new String[keys.size()]);
		}
	}

	private static final class MyControlEnabler implements ISelectionChangedListener {
		private final Class<?> requiredType;
		private final Control[] controls;
		private final MenuItem[] items;

		public MyControlEnabler(Class<?> type, Control[] controls) {
			this.requiredType = type;
			this.controls = controls;
			this.items = new MenuItem[0];
		}

		public MyControlEnabler(Class<?> type, MenuItem[] items) {
			this.requiredType = type;
			this.controls = new Control[0];
			this.items = items;
		}

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			boolean isEnabled = isSelected(event);
			for (Control control : controls) {
				control.setEnabled(isEnabled);
			}
			for (MenuItem item : items) {
				item.setEnabled(isEnabled);
			}
		}

		private boolean isSelected(SelectionChangedEvent event) {
			ISelection s = event.getSelection();
			IStructuredSelection ss = (IStructuredSelection) s;
			for (Object selected : ss.toList()) {
				if (requiredType.isInstance(selected)) {
					return true;
				}
			}
			return false;
		}
	}

	private ViewerUtil() {}

	public static void hookEnabledWithSelection(ISelectionProvider provider, Class<?> requiredType, Control... controls) {
		provider.addSelectionChangedListener(new MyControlEnabler(requiredType, controls));
	}

	public static void hookEnabledWithSelection(ISelectionProvider provider, Class<?> requiredType, MenuItem... items) {
		provider.addSelectionChangedListener(new MyControlEnabler(requiredType, items));
	}

	public static Object[] getSelectedObjects(ISelection selection) {
		return selection instanceof IStructuredSelection ? ((IStructuredSelection) selection).toArray() : new Object[0];
	}
	
	public static void hookContextMenu(IWorkbenchPart part, Viewer viewer) {
		IWorkbenchPartSite site = part.getSite();
		Control control = viewer.getControl();
		
		MenuManager menuManager = new MenuManager();
		menuManager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		Menu popup = menuManager.createContextMenu(control);
		control.setMenu(popup);
		
		site.registerContextMenu(menuManager, viewer);	// let other plug-ins contribute
		site.setSelectionProvider(viewer); 				// let manifest define selection-based expressions
		viewer.addSelectionChangedListener(new MyBridgeFromSelectionProviderToEvaluationService());
	}

	public static void handleOpen(OpenEvent event) {
		Object entity = getDistinctSelectedElement(event);
		WorkbenchUtil.openDistinct(entity);
	}

	private static Object getDistinctSelectedElement(OpenEvent event) {
		StructuredSelection selection = (StructuredSelection) event.getSelection();
		if (selection.size()>1) {
			throw new IllegalStateException("More than ine selected element: "+selection.toList());
		}
		return selection.getFirstElement();
	}
	
}