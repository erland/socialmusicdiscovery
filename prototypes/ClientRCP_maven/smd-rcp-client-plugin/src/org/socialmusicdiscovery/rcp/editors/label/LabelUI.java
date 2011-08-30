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

package org.socialmusicdiscovery.rcp.editors.label;


import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.socialmusicdiscovery.rcp.content.ObservableLabel;
import org.socialmusicdiscovery.rcp.views.util.AbstractComposite;

public class LabelUI extends AbstractComposite<ObservableLabel> {

	private Text nameText;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private ScrolledForm scrldfrmLabel;
	private Label nameLabel;
	private Section sctnLabelData;
	private CTabFolder tabFolder;
	private CTabItem releaseTab;
	private Composite composite;
	private Grid releaseGrid;
	private GridTableViewer releaseGridTableViewer;
	private GridColumn releaseColumn;
	private GridViewerColumn releaseGVC;
	private Label label;

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
		scrldfrmLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(scrldfrmLabel);
		scrldfrmLabel.setText("Label");
		scrldfrmLabel.getBody().setLayout(new GridLayout(1, false));
		
		nameLabel = new Label(scrldfrmLabel.getBody(), SWT.NONE);
		formToolkit.adapt(nameLabel, true, true);
		nameLabel.setText("Name");
		
		nameText = formToolkit.createText(scrldfrmLabel.getBody(), "text", SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		nameText.setText("");
		
		sctnLabelData = formToolkit.createSection(scrldfrmLabel.getBody(), Section.TWISTIE | Section.TITLE_BAR);
		sctnLabelData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(sctnLabelData);
		sctnLabelData.setText("Label Data");
		sctnLabelData.setExpanded(true);
		
		tabFolder = new CTabFolder(sctnLabelData, SWT.BORDER | SWT.FLAT | SWT.BOTTOM);
		formToolkit.adapt(tabFolder);
		formToolkit.paintBordersFor(tabFolder);
		sctnLabelData.setClient(tabFolder);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		releaseTab = new CTabItem(tabFolder, SWT.NONE);
		releaseTab.setText("Releases");
		
		composite = new Composite(tabFolder, SWT.NONE);
		releaseTab.setControl(composite);
		formToolkit.paintBordersFor(composite);
		composite.setLayout(new GridLayout(1, false));
		
		label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		formToolkit.adapt(label, true, true);
		label.setText("<NOT YET IMPLEMENTED>");
		
		releaseGridTableViewer = new GridTableViewer(composite, SWT.BORDER);
		releaseGrid = releaseGridTableViewer.getGrid();
		releaseGrid.setHeaderVisible(true);
		formToolkit.paintBordersFor(releaseGrid);
		
		releaseGVC = new GridViewerColumn(releaseGridTableViewer, SWT.NONE);
		releaseColumn = releaseGVC.getColumn();
		releaseColumn.setWidth(400);
		releaseColumn.setText("Release");
		
		
		initUI();
		}

	private void initUI() {
		tabFolder.setSelection(releaseTab);
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
