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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.services.IEvaluationService;
import org.socialmusicdiscovery.rcp.grid.GridViewerColumnComparator;

public class ViewerUtil {

	/**
	 * Not quite sure why, but it seems we need to monitor selection changes in
	 * internal viewers - the evaluation service is only updated when active
	 * part changes? And even more weird is that we need manually fire events
	 * when changing the active editor?
	 */
	private static final class MyBridgeFromSelectionProviderToEvaluationService extends AbstractSourceProvider implements ISelectionChangedListener, FocusListener {
		private static final String PROP = ISources.ACTIVE_CURRENT_SELECTION_NAME;
		private final Map<String, Object> map = new HashMap<String, Object>();
		private final Viewer viewer;

		private MyBridgeFromSelectionProviderToEvaluationService(Viewer viewer) {
			super();
			this.viewer = viewer;
			setCurrentSelection(null);
			IEvaluationService e = WorkbenchUtil.getEvaluationService();
			e.addSourceProvider(this);
			viewer.addSelectionChangedListener(this);
			viewer.getControl().addFocusListener(this);
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
			updateSelection(selection);
		}

		@Override
		public void focusGained(FocusEvent e) {
			ISelection selection = viewer.getSelection();
			updateSelection(selection);
		}
		
		private void updateSelection(ISelection selection) {
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

		@Override
		public void focusLost(FocusEvent arg0) {
			// TODO Auto-generated method stub
			
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
		new MyBridgeFromSelectionProviderToEvaluationService(viewer);
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

	/**
	 * Convenience method to allow passing properties as varargs.
	 * @param viewer
	 * @param list
	 * @param properties
	 */
	public static void bind(StructuredViewer viewer, IObservableList list, IBeanValueProperty... properties) {
		ViewerSupport.bind(viewer, list, properties);
	}

	/**
	 * Convenience method to allow passing properties as varargs.
	 * @param viewer
	 * @param set
	 * @param properties
	 */
	public static void bind(StructuredViewer viewer, IObservableSet set, IBeanValueProperty... properties) {
		ViewerSupport.bind(viewer, set, properties);
	}

	public static int resolveColumnIndex(GridViewerColumn gvc) {
		// TODO must be a better way?
		int columnIndex = 0;
		GridColumn column = gvc.getColumn();
		for (GridColumn c : column.getParent().getColumns()) {
			if (c==column) {
				break;
			}
			columnIndex++;
		}
		return columnIndex;
	}

	/**
	 * Hook default sorting on supplied columns. See note on
	 * {@link #hookSorter(Comparator, GridViewerColumn...)}.
	 * 
	 * @param gvcs
	 */
	public static void hookSorter(GridViewerColumn... gvcs) {
		for (GridViewerColumn gvc : gvcs) {
			GridViewerColumnComparator.hook(gvc);
		}
	}

	/**
	 * <p>
	 * Hook sorting on supplied columns using supplied comparator.
	 * </p>
	 * 
	 * <p>
	 * <b>Note</b>: the sorting is based on an observable label provider that
	 * typically is registered while binding the grid. Since the sorter registers 
	 * a selection listener on the grid, it can only be registered once. Hence, it 
	 * must be hooked in the UI constructor, and will resolve the label provider 
	 * on each call. 
	 * </p>
	 * 
	 * @param omparator
	 * @param gvcs
	 */
	public static void hookSorter(Comparator comparator, GridViewerColumn... gvcs) {
		for (GridViewerColumn gvc : gvcs) {
			GridViewerColumnComparator.hook(gvc, comparator);
		}
	}

	/**
	 * TODO - doesn't seem to update properly on multi-selections? Might be a
	 * problem with the Nebula grid when selecting many rows using Ctrl+click.
	 * 
	 * @param v
	 * @param controls
	 */
	public static void hookEnabledWithDistinctSelection(Viewer v, final Control... controls) {
		ISelectionChangedListener listener = new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object[] selected = getSelectedObjects(event.getSelection());
				boolean isEnabled = selected.length==1;
				for (Control control : controls) {
					control.setEnabled(isEnabled);
				}
			}
		};
		v.addSelectionChangedListener(listener);
	}
	
}