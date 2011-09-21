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

package org.socialmusicdiscovery.rcp.views.navigator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.socialmusicdiscovery.rcp.Activator;
import org.socialmusicdiscovery.rcp.content.DataSource;

public class NavigatorView extends ViewPart {

	private class MyDataSourceListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (ui!=null && evt.getSource() instanceof DataSource) {
				Boolean isConnected = (Boolean) evt.getNewValue();
				if (isConnected) {
					ui.setInput((DataSource) evt.getSource());
				} else {
					ui.setInput(null);
				}
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
		ui.setView(this);
		createActions();
		Activator.getDefault().getDataSource().initialize(getSite().getShell());
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
