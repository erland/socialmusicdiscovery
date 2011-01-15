package org.socialmusicdiscovery.rcp.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.socialmusicdiscovery.rcp.content.AbstractObservableEntity;
import org.socialmusicdiscovery.rcp.content.ObservableEntity;
import org.socialmusicdiscovery.rcp.util.TextUtil;
import org.socialmusicdiscovery.rcp.util.ViewerUtil;
import org.socialmusicdiscovery.rcp.views.util.AbstractComposite;

public abstract class AbstractEditorPart<T extends AbstractObservableEntity, U extends AbstractComposite<T>> extends EditorPart {

	private class MyDirtyStatusListener implements PropertyChangeListener {
		private final T observed;

		private MyDirtyStatusListener(T observed) {
			this.observed = observed;
			this.observed.addPropertyChangeListener(ObservableEntity.PROP_dirty, this);
		}

		public void dispose() {
			observed.removePropertyChangeListener(ObservableEntity.PROP_dirty, this);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
	       firePropertyChange(PROP_DIRTY);
	    }
	}

	private U ui;
	private MyDirtyStatusListener dirtyStatusListener;

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
		monitor.beginTask("Saving "+getPartName(), -1);
		MessageDialog.openWarning(getSite().getShell(), "Not Yet Implemented",
				"Sorry, save operation is not yet implemented. Will fake a successful save, but nothing will be written to database.");
		getEntity().setDirty(false);
	}

	/**
	 * @see #isSaveAsAllowed()
	 */
	@Override
	public void doSaveAs() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		hookDirtyStatusListener((T) input);
	}

	private void hookDirtyStatusListener(T entity) {
		if (dirtyStatusListener!=null) {
			dirtyStatusListener.dispose();
		}
		dirtyStatusListener = new MyDirtyStatusListener(entity);
	}

	@Override
	public boolean isDirty() {
		ObservableEntity entity = getEntity();
		return entity!=null && entity.isDirty();
	}

	protected U getUI() {
		return ui;
	}

	@SuppressWarnings("unchecked")
	protected T getEntity() {
		return (T) getEditorInput();
	}

	protected void hookContextMenus(Viewer... viewers) {
		for (Viewer v : viewers) {
			ViewerUtil.hookContextMenu(this, v);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		if (dirtyStatusListener!=null) {
			dirtyStatusListener.dispose();
		}
		super.dispose();
	}

}
