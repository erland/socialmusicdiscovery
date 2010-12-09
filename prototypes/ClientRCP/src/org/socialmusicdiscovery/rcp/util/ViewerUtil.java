package org.socialmusicdiscovery.rcp.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.socialmusicdiscovery.server.business.model.SMDEntity;

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

	public static SMDEntity<?>[] getSelectedEntities(ISelectionProvider viewer) {
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		return getSelectedEntities(selection);
	}

	public static SMDEntity<?>[] getSelectedEntities(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			return getSelectedEntities((IStructuredSelection) selection);
		}
		return new SMDEntity<?>[0];
	}
	
	public static SMDEntity<?>[] getSelectedEntities(IStructuredSelection selection) {
		List<SMDEntity<?>> result = new ArrayList<SMDEntity<?>>();
		if (selection!=null) {
			for (Object selected : selection.toList()) {
				if (selected instanceof SMDEntity<?>) {
					SMDEntity<?> entity = (SMDEntity<?>) selected;
					result.add(entity);
				}
			}
		}
		return (SMDEntity<?>[]) result.toArray(new SMDEntity<?>[result.size()]);
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

	
}