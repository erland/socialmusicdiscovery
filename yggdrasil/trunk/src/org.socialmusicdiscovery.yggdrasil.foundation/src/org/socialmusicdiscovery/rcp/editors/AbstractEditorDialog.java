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

package org.socialmusicdiscovery.rcp.editors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.socialmusicdiscovery.rcp.Activator;
import org.socialmusicdiscovery.rcp.content.ObservableEntity;
import org.socialmusicdiscovery.rcp.dialogs.EditorDialog;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Track;

/**
 * An abstract implementation of a {@link Dialog} for editing entities that
 * cannot be edited in a regular {@link AbstractEditorPart}. These are typically
 * 'dependent' entities that are not visible in the main navigation window, only
 * as 'children' of the main entities. A few examples of such entities are
 * {@link Track} and {@link Contributor}.
 * 
 * @author Peer Törngren
 */
public abstract class AbstractEditorDialog<T extends ObservableEntity> extends Dialog implements EditorDialog<T> {

	private final String title;
	protected Button okButton;

	public AbstractEditorDialog(Shell parentShell, String dialogTitle) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.title = dialogTitle;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText(title);
	}
	
	protected boolean openOK() {
		return openOK(this);
	}
	/**
	 * Open dialog and check return status. 
	 * @param dlg
	 * @return <code>true</code> if dialog was closed with OK button, <code>false</code> if not
	 */
	protected static boolean openOK(AbstractEditorDialog dlg) {
		return dlg.open() == Dialog.OK;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		okButton.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Implementation note: subclasses should typically <b>not</b> override this class, 
	 * but must implement {@link #beforeEdit(ObservableEntity)} and {@link #afterEdit()}.
	 */
	@Override
	public boolean edit(T entity) {
		beforeEdit(entity);
		if (open() == Dialog.OK) {
			T result = afterEdit();
			if (result.isDirty()) {
				Activator.getDefault().getDataSource().persist(new Shell(), result);
				return true;
			}
		}
		return false;
	}

	/**
	 * Setup the dialog to edit the supplied entity.
	 * @param entity
	 */
	protected abstract void beforeEdit(T entity);

	/**
	 * Get the edited result from this dialog, or <code>null</code>.
	 * This method is called when user has closed the dialog by pressing "OK".
	 * If dialog is cancelled, this method is <b>not</b> called. 
	 * @return A new or modified entity, or <code>null</code>
	 */
	protected abstract T afterEdit();
	
}