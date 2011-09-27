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
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.socialmusicdiscovery.yggdrasil.core.editors.ContributorPanel;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableWork;
import org.socialmusicdiscovery.yggdrasil.foundation.views.util.AbstractComposite;
import org.socialmusicdiscovery.yggdrasil.foundation.views.util.NotYetImplementedUI;

public class WorkUI extends AbstractComposite<ObservableWork> {

	private Text textName;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	protected ScrolledForm formWork;
	protected Text text;
	protected Section sectionWorkData;
	protected Composite compositeAlbumData;
	protected CTabFolder workDataTabFolder;
	protected GridColumn colPerformer;
	protected CTabItem contributorTab;
	private ContributorPanel artistPanel;
	private CTabItem tabItemRecordings;
	private Composite reordingsArea;
	private Label lblNewLabel;
	private NotYetImplementedUI notYetImplementedUI;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public WorkUI(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		formWork = formToolkit.createScrolledForm(this);
		GridData gd_formWork = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_formWork.widthHint = 1143;
		formWork.setLayoutData(gd_formWork);
		formToolkit.paintBordersFor(formWork);
		formWork.setText("Work");
		formWork.getBody().setLayout(new GridLayout(1, false));
		
		notYetImplementedUI = new NotYetImplementedUI(formWork.getBody(), SWT.NONE);
		notYetImplementedUI.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		formToolkit.adapt(notYetImplementedUI);
		formToolkit.paintBordersFor(notYetImplementedUI);
		
		Label lblName = formToolkit.createLabel(formWork.getBody(), "Name", SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		textName = formToolkit.createText(formWork.getBody(), "text", SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textName.setText("");
		
		
		sectionWorkData = formToolkit.createSection(formWork.getBody(), Section.TWISTIE | Section.TITLE_BAR);
		sectionWorkData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		formToolkit.paintBordersFor(sectionWorkData);
		sectionWorkData.setText("Work Data");
		sectionWorkData.setExpanded(true);
		
		compositeAlbumData = formToolkit.createComposite(sectionWorkData, SWT.NONE);
		formToolkit.paintBordersFor(compositeAlbumData);
		sectionWorkData.setClient(compositeAlbumData);
		compositeAlbumData.setLayout(new GridLayout(2, false));
		workDataTabFolder = new CTabFolder(compositeAlbumData, SWT.BORDER | SWT.BOTTOM);
		workDataTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		formToolkit.adapt(workDataTabFolder);
		formToolkit.paintBordersFor(workDataTabFolder);
		
		contributorTab = new CTabItem(workDataTabFolder, SWT.NONE);
		contributorTab.setText("Artist(s)");
		
		artistPanel = new ContributorPanel(workDataTabFolder, true);
		contributorTab.setControl(artistPanel);
		formToolkit.paintBordersFor(artistPanel);
		
		tabItemRecordings = new CTabItem(workDataTabFolder, SWT.NONE);
		tabItemRecordings.setText("Recordings");
		
		reordingsArea = formToolkit.createComposite(workDataTabFolder, SWT.NONE);
		tabItemRecordings.setControl(reordingsArea);
		formToolkit.paintBordersFor(reordingsArea);
		reordingsArea.setLayout(new GridLayout(1, false));
		
		lblNewLabel = formToolkit.createLabel(reordingsArea, "Place holder -  here we may show recordings  (etc)", SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));

		initUI();
		}

	private void initUI() {
		workDataTabFolder.setSelection	(contributorTab);

//		FIXME: make this work (also disable grid inputs)
//		ViewerUtil.hookEnabledWithDistinctSelection(gridViewerTracks, trackContributorPanel.getChildren());
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void afterSetModel(ObservableWork release) {
		getArtistPanel().setModel(getModel());
	}


	public ContributorPanel getArtistPanel() {
		return artistPanel;
	}

	protected boolean hasWork(Recording recording) {
		return !recording.getWorks().isEmpty();
	}

	/**
	 * @return {@link ObservableWork}
	 * @see #getModel()
	 */
	public ObservableWork getWork() {
		return getModel();
	}

	protected void initManualDataBindings(DataBindingContext bindingContext) {
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		//
		return bindingContext;
	}
}
