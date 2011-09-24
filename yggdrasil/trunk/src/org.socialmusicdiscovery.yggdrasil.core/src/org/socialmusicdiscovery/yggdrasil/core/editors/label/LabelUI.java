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

package org.socialmusicdiscovery.yggdrasil.core.editors.label;


import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.socialmusicdiscovery.rcp.content.ObservableLabel;
import org.socialmusicdiscovery.rcp.views.util.AbstractComposite;

public class LabelUI extends AbstractComposite<ObservableLabel> {

	private Text nameText;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private ScrolledForm scrldfrmLabel;
	private Label nameLabel;
	private Label infoLabel;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LabelUI(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		scrldfrmLabel = formToolkit.createScrolledForm(this);
		scrldfrmLabel.setToolTipText("");
		scrldfrmLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(scrldfrmLabel);
		scrldfrmLabel.setText("Label");
		scrldfrmLabel.getBody().setLayout(new GridLayout(1, false));
		
		infoLabel = new Label(scrldfrmLabel.getBody(), SWT.WRAP | SWT.HORIZONTAL);
		infoLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
		formToolkit.adapt(infoLabel, true, true);
		infoLabel.setText("The label editor is very simple - at the moment we only maintain the name of the label. In future, we may track other things like logo, URL to web page, etc.");
		
		nameLabel = new Label(scrldfrmLabel.getBody(), SWT.NONE);
		formToolkit.adapt(nameLabel, true, true);
		nameLabel.setText("Name:");
		
		nameText = formToolkit.createText(scrldfrmLabel.getBody(), "text", SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		nameText.setText("");
		
		
		initUI();
		}

	private void initUI() {
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public ObservableLabel getLabel() {
		return getModel();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue nameTextObserveTextObserveWidget = SWTObservables.observeText(nameText, SWT.Modify);
		IObservableValue getRecordingNameObserveValue = BeansObservables.observeValue(getLabel(), "name");
		bindingContext.bindValue(nameTextObserveTextObserveWidget, getRecordingNameObserveValue, null, null);
		//
		return bindingContext;
	}
}
