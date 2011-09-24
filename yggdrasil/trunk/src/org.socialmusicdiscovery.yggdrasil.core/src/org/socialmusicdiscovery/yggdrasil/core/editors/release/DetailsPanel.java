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

package org.socialmusicdiscovery.yggdrasil.core.editors.release;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.socialmusicdiscovery.rcp.content.ObservableLabel;
import org.socialmusicdiscovery.yggdrasil.core.editors.SelectionPanel;

/**
 * @author Peer Törngren
 *
 */
public class DetailsPanel extends Composite {
	
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Composite composite;
	private SelectionPanel<ObservableLabel> selectionPanel;
	private Label yearLabel;
	private Label label;
	private Text yearText;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public DetailsPanel(Composite parent, int style) {
		super(parent, style);
		setBackgroundMode(SWT.INHERIT_DEFAULT);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		composite = formToolkit.createComposite(this, SWT.NONE);
		formToolkit.paintBordersFor(composite);
		composite.setLayout(new GridLayout(1, false));
		
		label = new Label(composite, SWT.NONE);
		formToolkit.adapt(label, true, true);
		label.setText("<WORK IN PROGRESS: NOT FINISHED>");
		
		yearLabel = new Label(composite, SWT.NONE);
		yearLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		yearLabel.setToolTipText("Official release year  (as stated on media). This may be different than the original release date (e.g. a CD re-relase of a vinly LP).");
		formToolkit.adapt(yearLabel, true, true);
		yearLabel.setText("Year:");
		
		yearText = new Text(composite, SWT.BORDER);
		yearText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(yearText, true, true);
		
		selectionPanel = new SelectionPanel<ObservableLabel>(composite, SWT.NONE);
		selectionPanel.getText().setEditable(true);
		selectionPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		selectionPanel.getLabel().setToolTipText("Select recording label ");
		selectionPanel.getLabel().setText("Label:");
		formToolkit.adapt(selectionPanel);
		formToolkit.paintBordersFor(selectionPanel);
	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	public SelectionPanel<ObservableLabel> getSelectionPanel() {
		return selectionPanel;
	}
	public Text getYearText() {
		return yearText;
	}
}
