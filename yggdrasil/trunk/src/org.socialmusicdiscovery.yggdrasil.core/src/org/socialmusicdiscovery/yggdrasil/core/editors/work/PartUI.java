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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
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
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservablePart;
import org.socialmusicdiscovery.yggdrasil.foundation.util.WorkbenchUtil;
import org.socialmusicdiscovery.yggdrasil.foundation.views.util.AbstractComposite;
import org.socialmusicdiscovery.yggdrasil.foundation.views.util.NotYetImplementedUI;

public class PartUI extends AbstractComposite<ObservablePart> {
	private class MyParentLinkListener extends HyperlinkAdapter {

		@Override
		public void linkActivated(HyperlinkEvent e) {
			if (getModel()!=null && getModel().getParent()!=null) {
				WorkbenchUtil.openDistinct(getModel().getParent());
			}
		}

	}
	private Text textName;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	protected ScrolledForm formPart;
	protected Text text;
	private NotYetImplementedUI notYetImplementedUI;
	private WorkPanel workPanel;
	private Label parentLabel;
	private Hyperlink parentLink;
	
	private String parentName = null;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PartUI(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		formPart = formToolkit.createScrolledForm(this);
		GridData gd_formPart = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_formPart.widthHint = 1143;
		formPart.setLayoutData(gd_formPart);
		formToolkit.paintBordersFor(formPart);
		formPart.setText("Part");
		formPart.getBody().setLayout(new GridLayout(1, false));
		
		notYetImplementedUI = new NotYetImplementedUI(formPart.getBody(), SWT.NONE);
		notYetImplementedUI.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		formToolkit.adapt(notYetImplementedUI);
		formToolkit.paintBordersFor(notYetImplementedUI);
		
		Label lblName = formToolkit.createLabel(formPart.getBody(), "Name", SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		textName = formToolkit.createText(formPart.getBody(), "text", SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textName.setText("");
		
		parentLabel = formToolkit.createLabel(formPart.getBody(), "Parent (Part or Work)", SWT.NONE);
		
		parentLink = formToolkit.createHyperlink(formPart.getBody(), "(?)", SWT.NONE);
		parentLink.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		parentLink.setToolTipText("The parent part is the container of this (and presumably more) parts. The parent may itself be part of some other work.");
		
		formToolkit.paintBordersFor(parentLink);
		workPanel = new WorkPanel(formPart.getBody(), SWT.NONE);
		workPanel.dataSection.setText("Part Data");
		workPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.adapt(workPanel );
		formToolkit.paintBordersFor(workPanel );
		
		initUI();
		}


	private void initUI() {
		getParentLink().addHyperlinkListener(new MyParentLinkListener());
	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void afterSetModel(ObservablePart part) {
		getWorkPanel().setModel(getModel());
	}

	protected void initManualDataBindings(DataBindingContext bindingContext) {
		IObservableValue modelValue = BeansObservables.observeValue(getModel(), "parent.name"); // pick up changed parent or change name of parent! 
		IObservableValue targetValue = BeansObservables.observeValue(this, "parentName");
		bindingContext.bindValue(targetValue, modelValue, new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);
	}
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue textNameObserveTextObserveWidget = SWTObservables.observeText(textName, SWT.Modify);
		IObservableValue getWorkNameObserveValue = BeansObservables.observeValue(getModel(), "name");
		bindingContext.bindValue(textNameObserveTextObserveWidget, getWorkNameObserveValue, null, null);
		//
		return bindingContext;
	}

	public WorkPanel getWorkPanel() {
		return workPanel;
	}
	public Label getParentLabel() {
		return parentLabel;
	}
	public Hyperlink getParentLink() {
		return parentLink;
	}

	public String getParentName() {
		return parentName;
	}

	/**
	 * This is just a little trick to let us use standard databinding to monitor
	 * changes in parent or parent's name and keep the parent hyperlink updated.
	 * 
	 * @param parentName
	 */
	public void setParentName(String parentName) {
		this.parentName = parentName;
		getParentLink().setText(parentName);
	}
}
