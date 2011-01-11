package org.socialmusicdiscovery.rcp.views.navigator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.socialmusicdiscovery.rcp.Activator;
import org.socialmusicdiscovery.rcp.content.DataSource;
import org.socialmusicdiscovery.rcp.util.ViewerUtil;
import org.socialmusicdiscovery.rcp.views.util.OpenListener;

public class NavigatorView extends ViewPart {

	private class MyDataSourceListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (ui!=null && evt.getSource() instanceof DataSource) {
				ui.setInput((DataSource) evt.getSource());
			}
		}

	}

	public static final String ID = "org.socialmusicdiscovery.rcp.views.NavigatorView"; //$NON-NLS-1$
	private ShelfNavigator ui;

	public NavigatorView() {
		Activator.getDefault().getDataSource().addPropertyChangeListener(DataSource.PROP_IS_CONNECTED, new MyDataSourceListener());
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		ui = new ShelfNavigator(parent, SWT.NONE);
		hook(ui);
		createActions();
		ui.setInput(Activator.getDefault().getDataSource());
	}

	private void hook(ShelfNavigator ui) {
		ViewerUtil.hookContextMenu(this, ui.getTreeViewer());
		ui.getTreeViewer().addOpenListener(new OpenListener());
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	@Override
	public void setFocus() {
		// Set the focus
	}
}
