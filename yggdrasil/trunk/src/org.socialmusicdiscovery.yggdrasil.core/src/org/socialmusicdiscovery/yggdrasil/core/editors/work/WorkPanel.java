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
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.nebula.jface.gridviewer.GridTreeViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wb.rcp.databinding.BeansListObservableFactory;
import org.eclipse.wb.rcp.databinding.TreeBeanAdvisor;
import org.eclipse.wb.rcp.databinding.TreeObservableLabelProvider;
import org.socialmusicdiscovery.server.business.model.core.Part;
import org.socialmusicdiscovery.server.business.model.core.Work;
import org.socialmusicdiscovery.yggdrasil.core.editors.ContributorPanel;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableWork;
import org.socialmusicdiscovery.yggdrasil.foundation.views.util.AbstractComposite;

/**
 * Handles most details that are common to {@link Work} and {@link Part}, e.g.
 * parts and subparts, contributors etc. Specifically designed to be component
 * in {@link WorkUI} and {@link PartUI}.
 * 
 * @author Peer Törngren
 * 
 */
public class WorkPanel extends AbstractComposite<ObservableWork> {
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	protected ScrolledForm formWork;
	protected Text text;
	protected Section dataSection;
	protected Composite dataArea;
	protected CTabFolder dataTabFolder;
	protected GridColumn colPerformer;
	protected CTabItem contributorTab;
	private ContributorPanel artistPanel;
	private CTabItem tabItemRecordings;
	private Composite reordingsArea;
	private Label lblRecordings;
	private Section sctnParts;
	private Composite composite;
	private CTabItem tbtmDetails;
	private Composite detailsArea;
	private Grid partsGrid;
	private GridTreeViewer partsViewer;
	private GridViewerColumn partGVC;
	private GridColumn partColumn;
	private SashForm sashForm;
	private Section partDetailsSection;
	private Composite partsArea;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public WorkPanel(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		sctnParts = formToolkit.createSection(this, Section.TITLE_BAR);
		sctnParts.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(sctnParts);
		sctnParts.setText("Parts");
		
		composite = formToolkit.createComposite(sctnParts, SWT.NONE);
		formToolkit.paintBordersFor(composite);
		sctnParts.setClient(composite);
		composite.setLayout(new GridLayout(1, false));
		
		sashForm = new SashForm(composite, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.adapt(sashForm);
		formToolkit.paintBordersFor(sashForm);
		
		partsArea = formToolkit.createComposite(sashForm, SWT.NONE);
		formToolkit.paintBordersFor(partsArea);
		GridLayout gl_partsArea = new GridLayout(1, false);
		gl_partsArea.marginHeight = 0;
		gl_partsArea.marginWidth = 0;
		partsArea.setLayout(gl_partsArea);
		
		partsViewer = new GridTreeViewer(partsArea, SWT.BORDER);
		partsGrid = partsViewer.getGrid();
		partsGrid.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		partsGrid.setHeaderVisible(true);
		formToolkit.paintBordersFor(partsGrid);
		
		partGVC = new GridViewerColumn(partsViewer, SWT.NONE);
		partColumn = partGVC.getColumn();
		partColumn.setTree(true);
		partColumn.setWidth(400);
		partColumn.setText("Part");
		
		partDetailsSection = formToolkit.createSection(sashForm, Section.TITLE_BAR);
		formToolkit.paintBordersFor(partDetailsSection);
		partDetailsSection.setText("Part Details");
		partDetailsSection.setExpanded(true);
		
		WorkContributorPanel workContributorPanel = new WorkContributorPanel(partDetailsSection, SWT.NONE);
		formToolkit.adapt(workContributorPanel);
		formToolkit.paintBordersFor(workContributorPanel);
		partDetailsSection.setClient(workContributorPanel);
		sashForm.setWeights(new int[] {428, 822});
		
		
		dataSection = formToolkit.createSection(this, Section.TWISTIE | Section.TITLE_BAR);
		dataSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(dataSection);
		dataSection.setText("<title to be replace by container>");
		dataSection.setExpanded(true);
		
		dataArea = formToolkit.createComposite(dataSection, SWT.NONE);
		formToolkit.paintBordersFor(dataArea);
		dataSection.setClient(dataArea);
		dataArea.setLayout(new GridLayout(1, false));
		dataTabFolder = new CTabFolder(dataArea, SWT.BORDER | SWT.BOTTOM);
		dataTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		formToolkit.adapt(dataTabFolder);
		formToolkit.paintBordersFor(dataTabFolder);
		
		contributorTab = new CTabItem(dataTabFolder, SWT.NONE);
		contributorTab.setText("Artist(s)");
		
		artistPanel = new ContributorPanel(dataTabFolder, true);
		contributorTab.setControl(artistPanel);
		formToolkit.paintBordersFor(artistPanel);
		
		tabItemRecordings = new CTabItem(dataTabFolder, SWT.NONE);
		tabItemRecordings.setText("Recordings");
		
		reordingsArea = formToolkit.createComposite(dataTabFolder, SWT.NONE);
		tabItemRecordings.setControl(reordingsArea);
		formToolkit.paintBordersFor(reordingsArea);
		reordingsArea.setLayout(new GridLayout(1, false));
		
		lblRecordings = formToolkit.createLabel(reordingsArea, "Place holder -  here we may show recordings  (etc)", SWT.NONE);
		lblRecordings.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		
		tbtmDetails = new CTabItem(dataTabFolder, SWT.NONE);
		tbtmDetails.setText("Details");
		
		detailsArea = formToolkit.createComposite(dataTabFolder, SWT.NONE);
		tbtmDetails.setControl(detailsArea);
		formToolkit.paintBordersFor(detailsArea);
		detailsArea.setLayout(new GridLayout(1, false));
		
		Label lblDetails = formToolkit.createLabel(detailsArea, "Placeholder - here we will show details about the work; originally composed, links to scores, etc", SWT.NONE);
		lblDetails.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		new Label(dataArea, SWT.NONE);

		initUI();
		}

	private void initUI() {
		dataTabFolder.setSelection	(contributorTab);

//		FIXME: make this work (also disable grid inputs)
//		ViewerUtil.hookEnabledWithDistinctSelection(gridViewerTracks, trackContributorPanel.getChildren());
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		BeansListObservableFactory treeObservableFactory = new BeansListObservableFactory(ObservableWork.class, "parts");
		TreeBeanAdvisor treeAdvisor = new TreeBeanAdvisor(ObservableWork.class, null, "parts", null);
		ObservableListTreeContentProvider treeContentProvider = new ObservableListTreeContentProvider(treeObservableFactory, treeAdvisor);
		partsViewer.setContentProvider(treeContentProvider);
		//
		partsViewer.setLabelProvider(new TreeObservableLabelProvider(treeContentProvider.getKnownElements(), ObservableWork.class, "name", null));
		//
		IObservableList getWorkPartsObserveList = BeansObservables.observeList(Realm.getDefault(), getModel(), "parts");
		partsViewer.setInput(getWorkPartsObserveList);
//		partsViewer.setInput(getModel().getParts());
		//
		return bindingContext;
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public Grid getPartsGrid() {
		return partsGrid;
	}
	public GridTreeViewer getPartsViewer() {
		return partsViewer;
	}
}
