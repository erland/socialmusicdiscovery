package org.socialmusicdiscovery.rcp.views.navigator;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.socialmusicdiscovery.rcp.content.DataSource;
import org.socialmusicdiscovery.rcp.views.util.OpenListener;

public class NavigatorView extends ViewPart {

	public static final String ID = "org.socialmusicdiscovery.rcp.views.NavigatorView"; //$NON-NLS-1$

	public NavigatorView() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		NavigatorUI ui = new NavigatorUI(parent, SWT.NONE);
		hook(ui);
		createActions();
		initializeToolBar();
		initializeMenu();
		ui.setInput(new DataSource());
	}

	private void hook(NavigatorUI ui) {
		ui.getTreeViewer().addOpenListener(new OpenListener());
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}
}
