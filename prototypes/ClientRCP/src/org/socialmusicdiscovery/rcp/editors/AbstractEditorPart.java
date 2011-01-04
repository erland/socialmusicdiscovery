package org.socialmusicdiscovery.rcp.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.socialmusicdiscovery.rcp.content.AbstractObservableEntity;
import org.socialmusicdiscovery.rcp.util.TextUtil;
import org.socialmusicdiscovery.rcp.util.ViewerUtil;
import org.socialmusicdiscovery.rcp.views.util.AbstractComposite;

public abstract class AbstractEditorPart<T extends AbstractObservableEntity, U extends AbstractComposite<T>> extends EditorPart {

	private U ui;

	public AbstractEditorPart() {
	}

	public void createPartControl(Composite parent, U ui) {
		this.ui = ui;
		T model = resolveModel();
		model.inflate();
		ui.setModel(model);
		ui.setPart(this);
		setPartName(getShortName(model));
	}

	@SuppressWarnings("unchecked")
	private T resolveModel() {
		return (T) getEditorInput();
	}

	protected String getShortName(T model) {
		return TextUtil.getShortText(model.getName());
	}

	@Override
	public void setFocus() {
		ui.setFocus();
	}

	/**
	 * Convenience method.
	 * @return {@link IToolBarManager}
	 */
	protected IToolBarManager getToolbarManager() {
		return getEditorSite().getActionBars().getToolBarManager();
	}

	/**
	 * Convenience method.
	 * @return {@link IMenuManager}
	 */
	protected IMenuManager getMenuManager() {
		return getEditorSite().getActionBars().getMenuManager();
	}
	@Override
	public void doSave(IProgressMonitor monitor) {
		// Do the Save operation
	}

	@Override
	public void doSaveAs() {
		// Do the Save As operation
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	protected U getUI() {
		return ui;
	}

	protected void hookContextMenus(Viewer... viewers) {
		for (Viewer v : viewers) {
			ViewerUtil.hookContextMenu(this, v);
		}
	}


}
