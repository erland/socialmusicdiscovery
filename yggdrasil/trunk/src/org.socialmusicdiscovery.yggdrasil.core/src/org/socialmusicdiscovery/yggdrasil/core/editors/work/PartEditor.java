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

package org.socialmusicdiscovery.yggdrasil.core.editors.work;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservablePart;
import org.socialmusicdiscovery.yggdrasil.foundation.editors.AbstractEditorPart;

/**
* <p>
 * Menu IDs (suffix to the part name) are defined as constants, as recommended
 * by
 * {@link IWorkbenchPartSite#registerContextMenu(org.eclipse.jface.action.MenuManager, org.eclipse.jface.viewers.ISelectionProvider)}:
 * <ul>
 * <li>{@link #MENU_ID_CONTRIBUTORS}</li>
 * </ul>
 * </p>
 * @author Peer Törngren
 * 
 */
public class PartEditor extends AbstractEditorPart<ObservablePart, PartUI> {
	public PartEditor() {
	}

	public static final String ID = PartEditor.class.getName();
//	private static final String MENU_ID_CONTRIBUTORS = ContributorPanel.MENU_ID;

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent, new PartUI(parent, SWT.NONE));
//		hookContextMenus();
	}

//	private void hookContextMenus() {
//		ViewerUtil.hookContextMenu(this, MENU_ID_CONTRIBUTORS, 
//				getUI().getArtistPanel().getGridViewer()
//		);
//	}
//
}
