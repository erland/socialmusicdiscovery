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

package org.socialmusicdiscovery.yggdrasil.foundation.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.ISaveablesSource;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.services.IEvaluationService;
import org.socialmusicdiscovery.yggdrasil.foundation.Activator;
import org.socialmusicdiscovery.yggdrasil.foundation.content.AbstractEditableEntity;
import org.socialmusicdiscovery.yggdrasil.foundation.content.AbstractObservableEntity;
import org.socialmusicdiscovery.yggdrasil.foundation.content.DataSource;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableEntity;
import org.socialmusicdiscovery.yggdrasil.foundation.util.ViewerUtil;
import org.socialmusicdiscovery.yggdrasil.foundation.util.WorkbenchUtil;
import org.socialmusicdiscovery.yggdrasil.foundation.views.util.AbstractComposite;

/**
 * <p>
 * An abstract implementation of an entity editor. All concrete editors are expected to
 * extend this class. This class is the hook into the workbench, and holds very
 * little of the actual UI; it instantiates a root composite and sets the editor
 * input on this composite.
 * </p>
 * <p>
 * Design note:<br>
 * At the moment, the class implements {@link ISaveablePart2} since this was the
 * simplest/fastest way to implement "abort changes". However, it offers a less
 * pleasing behavior on exit; user must respond to one prompt for each dirty
 * editor. We should really implement {@link ISaveablesSource} instead.
 * </p>
 * 
 * @author Peer Törngren
 * 
 * @param <T>
 *            the core interface of the entity we edit
 * @param <U>
 *            the main UI we launch in the editor
 */
public abstract class AbstractEditorPart<T extends AbstractEditableEntity, U extends AbstractComposite<T>> extends EditorPart  {

	private class MyModelListener implements PropertyChangeListener {
		private final T observed;

		private MyModelListener(T observed) {
			this.observed = observed;
			this.observed.addPropertyChangeListener(ObservableEntity.PROP_dirty, this);
			this.observed.addPropertyChangeListener(ObservableEntity.PROP_name, this);
		}

		private void dispose() {
			observed.removePropertyChangeListener(ObservableEntity.PROP_dirty, this);
			observed.addPropertyChangeListener(ObservableEntity.PROP_name, this);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			String propertyName = evt.getPropertyName();
			if (ObservableEntity.PROP_dirty.equals(propertyName)) {
		       firePropertyChange(ISaveablePart2.PROP_DIRTY);
		       updateBackup((Boolean) evt.getNewValue());
			} else if (ObservableEntity.PROP_name.equals(propertyName)) {
				updatePartName();
			} else {
				throw new IllegalArgumentException("Unexpected property name: "+propertyName); 
			}
	    }

		private void updateBackup(Boolean isDirty) {
			if (!isDirty.booleanValue()) {
				createBackup();
			}
		}
	}

	private U ui;
	private MyModelListener modelListener;
	private AbstractEditableEntity original;

	public AbstractEditorPart() {
	}

	public void createPartControl(Composite parent, U ui) {
		this.ui = ui;
		T model = resolveModel();
		inflate(model);
		ui.setModel(model);
		ui.setPart(this);
		updatePartName();
	}

	/**
	 * If necessary, "inflate" the model object by filling all properties.
	 * Subclasses may override or extend as necessary, the default
	 * implementation calls {@link AbstractObservableEntity#inflate()}.
	 * 
	 * @param model
	 */
	protected void inflate(T model) {
		model.inflate();
	}

	private T resolveModel() {
		return (T) getEditorInput();
	}

	private void updatePartName() {
		setPartName(getModelName());
	}

	protected String getModelName() {
		String name = ui.getModel().getName();
		return name==null ? "?" : name;
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
		DataSource dataSource = Activator.getDefault().getDataSource();
		boolean isPersistedOK = dataSource.persist(getShell(), getEntity());
		if (isPersistedOK) {
			monitor.worked(1);
		} else {
			monitor.setCanceled(true);
		}
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

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		hookModelListener(getEntity());
		this.original = getEntity().backup();
		assert !original.isDirty() : "Original dirty on entry: "+original;
	}

	private void hookModelListener(T entity) {
		if (modelListener!=null) {
			modelListener.dispose();
		}
		modelListener = new MyModelListener(entity);
	}

	@Override
	public boolean isDirty() {
		ObservableEntity entity = getEntity();
		return entity!=null && entity.isDirty();
	}

	protected U getUI() {
		return ui;
	}

	protected T getEntity() {
		T entity = (T) getEditorInput().getAdapter(AbstractEditableEntity.class);
		assert getEditorInput()==null || entity!=null : "Input is not an "+AbstractObservableEntity.class+": "+getEditorInput();
		return entity;
	}

	/**
	 * Subclasses are expected to call this method from
	 * {@link #createPartControl(Composite)} in order to register supplied
	 * viewers as {@link ISelectionProvider}s for the Command framework and
	 * {@link IEvaluationService}. This allows the service to enable/disable
	 * commands and popup menus when the selections in the supplied viewers
	 * change.
	 * 
	 * @param viewers
	 */
	protected void hookContextMenus(Viewer... viewers) {
		for (Viewer v : viewers) {
			ViewerUtil.hookContextMenu(this, v);
		}
	}

	@Override
	public void dispose() {
		if (modelListener!=null) {
			modelListener.dispose();
		}
		// Did user choose not to save changes?
		if (isDirty() && isLastActiveEditor()) {
			undoAllChanges();
		}
		super.dispose();
	}

	private boolean isLastActiveEditor() {
		Set<IEditorReference> editors = WorkbenchUtil.findActiveEditorsInAnyWorkbenchWindow(getEditorInput());
		return editors.isEmpty();
	}

//	/**
//	 * @see ISaveablesSource
//	 */
//	@Override
//	public int promptToSaveOnClose() {
//		String dialogTitle = "Save Entity";
//		Image dialogImage = getDefaultImage();
//		String dialogMessage = "'"+getEntity().getName()+"' has been modified. Save changes?";
//		String[] dialogButtonLabels = { 
//				IDialogConstants.YES_LABEL,
//                IDialogConstants.NO_LABEL,
//                IDialogConstants.CANCEL_LABEL 
//        };
//		
//		MessageDialog dlg = new MessageDialog(getShell(), dialogTitle, dialogImage, dialogMessage , SWT.SHEET, dialogButtonLabels, 0);
//		switch (dlg.open()) {
//		case 0:
//			return ISaveablePart2.YES;
//
//		case 1:
//			undoAllChanges();
//			return ISaveablePart2.NO;
//
//		case 2:
//			return ISaveablePart2.CANCEL;
//
//		default:
//			return ISaveablePart2.DEFAULT; // should not happen
//		}
////		return ISaveablePart2.DEFAULT;
//	}

	protected void undoAllChanges() {
		getEntity().restore(original);
	}
	
	protected void createBackup() {
		original = getEntity().backup();
	}

	protected Shell getShell() {
		return getSite().getShell();
	}
}
