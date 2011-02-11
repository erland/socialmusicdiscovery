package org.socialmusicdiscovery.rcp.views;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public abstract class AbstractViewPart extends ViewPart {

	public AbstractViewPart() {
	}

	@Override
	public void createPartControl(Composite parent) {
	}

	@Override
	public void setFocus() {
	}

	/**
	 * Convenience method.
	 * @return {@link IToolBarManager}
	 */
	protected IToolBarManager getToolbarManager() {
		return getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Convenience method.
	 * @return {@link IMenuManager}
	 */
	protected IMenuManager getMenuManager() {
		return getViewSite().getActionBars().getMenuManager();
	}

}
