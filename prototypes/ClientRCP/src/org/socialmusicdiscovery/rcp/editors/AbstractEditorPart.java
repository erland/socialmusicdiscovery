package org.socialmusicdiscovery.rcp.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.socialmusicdiscovery.rcp.util.TextUtil;
import org.socialmusicdiscovery.rcp.views.util.AbstractComposite;
import org.socialmusicdiscovery.rcp.views.util.EntityLabelProvider;
import org.socialmusicdiscovery.rcp.views.util.SMDEditorInput;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

public abstract class AbstractEditorPart<T extends SMDEntity<?>> extends EditorPart {

	private T entity;
	private AbstractComposite<T> ui;

	public AbstractEditorPart() {
	}

	public void createPartControl(Composite parent, AbstractComposite<T> ui) {
		setPartName(getShortName(entity));
		this.ui = ui;
		ui.setEntity(getEntity());
	}

	protected String getShortName(T entity) {
		return TextUtil.getShortText(EntityLabelProvider.getText(entity));
	}

	@Override
	public void setFocus() {
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

	@SuppressWarnings("unchecked")
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		SMDEditorInput smdInput = (SMDEditorInput) input; 
		setEntity((T) smdInput.getEntity());
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	protected void setEntity(T entity) {
		this.entity = entity;
	}

	protected T getEntity() {
		return entity;
	}

	protected AbstractComposite<T> getUi() {
		return ui;
	}


}
