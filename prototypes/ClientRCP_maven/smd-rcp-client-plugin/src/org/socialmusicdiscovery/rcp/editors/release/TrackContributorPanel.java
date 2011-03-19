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

package org.socialmusicdiscovery.rcp.editors.release;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.socialmusicdiscovery.rcp.content.ObservableTrack;
import org.socialmusicdiscovery.rcp.editors.widgets.ContributorPanel;
import org.socialmusicdiscovery.rcp.views.util.AbstractComposite;

public class TrackContributorPanel extends AbstractComposite<ObservableTrack> {
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	protected Text text;
	protected GridColumn colPerformer;
	private ContributorPanel contributorPanel;
	private ToolBar toolBar;
	private ToolItem recordingCheck;
	private ToolItem workCheck;
	private ToolItem albumCheck;
	private ToolItem sessionCheck;
	private Button effectiveContributorsButton;
	private Label separator;
	private Section rootSection;
	private Composite rootArea;
	private Section filterSection;
	private Composite composite;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TrackContributorPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		rootSection = formToolkit.createSection(this, Section.TWISTIE | Section.TITLE_BAR);
		formToolkit.paintBordersFor(rootSection);
		rootSection.setText("Track Contributors");
		rootSection.setExpanded(true);
		
		rootArea = formToolkit.createComposite(rootSection, SWT.NONE);
		formToolkit.paintBordersFor(rootArea);
		rootSection.setClient(rootArea);
		rootArea.setLayout(new GridLayout(2, false));
		
		contributorPanel = new ContributorPanel(rootArea, SWT.NONE);
		contributorPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.adapt(contributorPanel);
		formToolkit.paintBordersFor(contributorPanel);
		
		filterSection = formToolkit.createSection(rootArea, Section.TWISTIE | Section.TITLE_BAR);
		filterSection.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		formToolkit.paintBordersFor(filterSection);
		filterSection.setText("Filter");
		filterSection.setExpanded(true);
		
		composite = formToolkit.createComposite(filterSection, SWT.NONE);
		formToolkit.paintBordersFor(composite);
		filterSection.setClient(composite);
		composite.setLayout(new GridLayout(1, false));
		
		toolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT | SWT.VERTICAL);
		formToolkit.adapt(toolBar);
		formToolkit.paintBordersFor(toolBar);
		
		albumCheck = new ToolItem(toolBar, SWT.CHECK);
		albumCheck.setToolTipText("Show contributors defined for Album");
		albumCheck.setText("Album");
		
		sessionCheck = new ToolItem(toolBar, SWT.CHECK);
		sessionCheck.setToolTipText("Show contributors defined for Recording Session");
		sessionCheck.setText("Session");
		
		recordingCheck = new ToolItem(toolBar, SWT.CHECK);
		recordingCheck.setToolTipText("Show contributors defined for Recording");
		recordingCheck.setText("Recording");
		
		workCheck = new ToolItem(toolBar, SWT.CHECK);
		workCheck.setToolTipText("Show contributors defined for Work");
		workCheck.setText("Work");
		
		separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		formToolkit.adapt(separator, true, true);
		
		effectiveContributorsButton = new Button(composite, SWT.CHECK);
		formToolkit.adapt(effectiveContributorsButton, true, true);
		effectiveContributorsButton.setText("Effective");
		new Label(rootArea, SWT.NONE);

		initUI();
		}

	private void initUI() {
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		return bindingContext;
	}

	@Override
	protected void afterSetModel(ObservableTrack model) {
		super.afterSetModel(model);
		System.out.println("TrackContributorPanel.afterSetModel(): "+model);
	}
}
