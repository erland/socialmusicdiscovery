package org.socialmusicdiscovery.rcp.editors.release;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

public class ViewerUtil {

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
}