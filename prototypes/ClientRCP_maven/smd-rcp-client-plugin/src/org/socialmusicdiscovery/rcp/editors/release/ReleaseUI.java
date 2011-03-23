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
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.socialmusicdiscovery.rcp.content.ObservableRelease;
import org.socialmusicdiscovery.rcp.content.ObservableTrack;
import org.socialmusicdiscovery.rcp.editors.widgets.ContributorPanel;
import org.socialmusicdiscovery.rcp.editors.widgets.TrackMediumNumberComparator;
import org.socialmusicdiscovery.rcp.editors.widgets.TrackNumberComparator;
import org.socialmusicdiscovery.rcp.util.ViewerUtil;
import org.socialmusicdiscovery.rcp.views.util.AbstractComposite;
import org.socialmusicdiscovery.rcp.views.util.OpenListener;
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.UpdateValueStrategy;

public class ReleaseUI extends AbstractComposite<ObservableRelease> {

	private Text textName;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	protected ScrolledForm scrldfrmRelease;
	protected Text text;
	protected Section sectionAlbumData;
	protected Composite compositeAlbumData;
	protected CTabFolder albumDataTabFolder;
	protected Grid gridTracks;
	private GridTableViewer gridViewerTracks;
	protected GridColumn colTrackNumber;
	private GridViewerColumn gvcTrackNumber;
	protected GridColumn colTitle;
	private GridViewerColumn gvcTitle;
	protected GridColumn colPerformer;
	protected CTabItem contributorTab;
	private Section sctnTracks;
	private Composite gridContainer;
	private GridColumnGroup groupContributors;
	private ContributorPanel artistPanel;
	private GridColumn colMediumNbr;
	private GridViewerColumn gvcMediumNbr;
	private GridColumnGroup groupNumbers;
	private SashForm sashForm;
	private CTabItem tabItemReleases;
	private Composite releasesArea;
	private Label lblNewLabel;
	private CTabItem itemDetails;
	private Composite labelArea;
	private Label lblPlaceHolder;
	private Composite tracksArea;
	private TrackContributorPanel trackContributorPanel;
	private Composite composite;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ReleaseUI(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		scrldfrmRelease = formToolkit.createScrolledForm(this);
		GridData gd_scrldfrmRelease = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_scrldfrmRelease.widthHint = 1143;
		scrldfrmRelease.setLayoutData(gd_scrldfrmRelease);
		formToolkit.paintBordersFor(scrldfrmRelease);
		scrldfrmRelease.setText("Release");
		scrldfrmRelease.getBody().setLayout(new GridLayout(1, false));
		
		Label lblName = formToolkit.createLabel(scrldfrmRelease.getBody(), "Name", SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		textName = formToolkit.createText(scrldfrmRelease.getBody(), "text", SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textName.setText("");
		
		sctnTracks = formToolkit.createSection(scrldfrmRelease.getBody(), Section.EXPANDED | Section.TITLE_BAR);
		sctnTracks.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(sctnTracks);
		sctnTracks.setText("Tracks");
		
		gridContainer = formToolkit.createComposite(sctnTracks, SWT.NONE);
		formToolkit.paintBordersFor(gridContainer);
		sctnTracks.setClient(gridContainer);
		FillLayout fl_gridContainer = new FillLayout(SWT.HORIZONTAL);
		fl_gridContainer.marginWidth = 5;
		fl_gridContainer.spacing = 5;
		gridContainer.setLayout(fl_gridContainer);
		
		sashForm = new SashForm(gridContainer, SWT.BORDER);
		sashForm.setSashWidth(2);
		formToolkit.adapt(sashForm);
		formToolkit.paintBordersFor(sashForm);
		
		composite = formToolkit.createComposite(sashForm, SWT.NONE);
		formToolkit.paintBordersFor(composite);
		composite.setLayout(new GridLayout(1, false));
		
		tracksArea = formToolkit.createComposite(composite, SWT.NONE);
		tracksArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(tracksArea);
		FillLayout fl_tracksArea = new FillLayout(SWT.HORIZONTAL);
		fl_tracksArea.marginWidth = 5;
		tracksArea.setLayout(fl_tracksArea);
		
		gridViewerTracks = new GridTableViewer(tracksArea, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		gridTracks = gridViewerTracks.getGrid();
		gridTracks.setCellSelectionEnabled(true);
		gridTracks.setHeaderVisible(true);
		formToolkit.paintBordersFor(gridTracks);
		
		groupNumbers = new GridColumnGroup(gridTracks, SWT.TOGGLE);
		groupNumbers.setText("Index");
		
		colMediumNbr = new GridColumn(groupNumbers, SWT.RIGHT);
		colMediumNbr.setDetail(false);
		colMediumNbr.setMoveable(true);
		colMediumNbr.setVisible(true);
		colMediumNbr.setWidth(75);
		colMediumNbr.setText("Medium#");
		gvcMediumNbr = new GridViewerColumn(gridViewerTracks, colMediumNbr);
		
		colTrackNumber = new GridColumn(groupNumbers, SWT.RIGHT);
		colTrackNumber.setMoveable(true);
		colTrackNumber.setWidth(75);
		colTrackNumber.setText("Track#");
		gvcTrackNumber = new GridViewerColumn(gridViewerTracks, colTrackNumber);
		
		gvcTitle = new GridViewerColumn(gridViewerTracks, SWT.NONE);
		colTitle = gvcTitle.getColumn();
		colTitle.setMoveable(true);
		colTitle.setWidth(300);
		colTitle.setText("Title");
		
		groupContributors = new GridColumnGroup(gridTracks, SWT.NONE);
		groupContributors.setText("Contributors");
		
		trackContributorPanel = new TrackContributorPanel(sashForm, SWT.NONE);
		formToolkit.adapt(trackContributorPanel);
		formToolkit.paintBordersFor(trackContributorPanel);
		sashForm.setWeights(new int[] {404, 714});
		
		
		sectionAlbumData = formToolkit.createSection(scrldfrmRelease.getBody(), Section.TWISTIE | Section.TITLE_BAR);
		sectionAlbumData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		formToolkit.paintBordersFor(sectionAlbumData);
		sectionAlbumData.setText("Album Data");
		sectionAlbumData.setExpanded(true);
		
		compositeAlbumData = formToolkit.createComposite(sectionAlbumData, SWT.NONE);
		formToolkit.paintBordersFor(compositeAlbumData);
		sectionAlbumData.setClient(compositeAlbumData);
		compositeAlbumData.setLayout(new GridLayout(2, false));
		albumDataTabFolder = new CTabFolder(compositeAlbumData, SWT.BORDER | SWT.BOTTOM);
		albumDataTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		formToolkit.adapt(albumDataTabFolder);
		formToolkit.paintBordersFor(albumDataTabFolder);
		
		contributorTab = new CTabItem(albumDataTabFolder, SWT.NONE);
		contributorTab.setText("Artist(s)");
		
		artistPanel = new ContributorPanel(albumDataTabFolder, true);
		contributorTab.setControl(artistPanel);
		formToolkit.paintBordersFor(artistPanel);
		
		tabItemReleases = new CTabItem(albumDataTabFolder, SWT.NONE);
		tabItemReleases.setText("Releases");
		
		releasesArea = formToolkit.createComposite(albumDataTabFolder, SWT.NONE);
		tabItemReleases.setControl(releasesArea);
		formToolkit.paintBordersFor(releasesArea);
		releasesArea.setLayout(new GridLayout(1, false));
		
		lblNewLabel = formToolkit.createLabel(releasesArea, "Place holder -  here we will show master, parent, and child releases (etc)", SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		
		itemDetails = new CTabItem(albumDataTabFolder, SWT.NONE);
		itemDetails.setText("Details");
		
		labelArea = formToolkit.createComposite(albumDataTabFolder, SWT.NONE);
		itemDetails.setControl(labelArea);
		formToolkit.paintBordersFor(labelArea);
		labelArea.setLayout(new GridLayout(1, false));
		
		lblPlaceHolder = formToolkit.createLabel(labelArea, "Place holder -  here we will show release details - date, label, numbers etc", SWT.NONE);
		lblPlaceHolder.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));

		initUI();
		}

	private void initUI() {
		albumDataTabFolder.setSelection	(contributorTab);
		gridViewerTracks.addOpenListener(new OpenListener()); // default edit (double-click)
//		FIXME: make this work (also disable grid inputs)
//		ViewerUtil.hookEnabledWithDistinctSelection(gridViewerTracks, trackContributorPanel.getChildren());
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void afterSetModel(ObservableRelease release) {
		getArtistPanel().setModel(getModel());
		ViewerUtil.bindSorter(gvcTitle);
		ViewerUtil.bindSorter(new TrackMediumNumberComparator(),  gvcMediumNbr);
		ViewerUtil.bindSorter(new TrackNumberComparator(),  gvcTrackNumber);
	}


	public ContributorPanel getArtistPanel() {
		return artistPanel;
	}

	public GridTableViewer getGridViewerTracks() {
		return gridViewerTracks;
	}

	protected boolean hasWork(Recording recording) {
		return !recording.getWorks().isEmpty();
	}

	/**
	 * @return {@link ObservableRelease}
	 * @see #getModel()
	 */
	public ObservableRelease getRelease() {
		return getModel();
	}
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue textNameObserveTextObserveWidget = SWTObservables.observeText(textName, SWT.Modify);
		IObservableValue artistgetNameEmptyObserveValue = PojoObservables.observeValue(getModel(), "name");
		bindingContext.bindValue(textNameObserveTextObserveWidget, artistgetNameEmptyObserveValue, null, null);
		//
		IObservableValue textNameObserveTooltipTextObserveWidget = SWTObservables.observeTooltipText(textName);
		IObservableValue getModelToolTipTextObserveValue = PojoObservables.observeValue(getModel(), "toolTipText");
		bindingContext.bindValue(textNameObserveTooltipTextObserveWidget, getModelToolTipTextObserveValue, null, null);
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		gridViewerTracks.setContentProvider(listContentProvider);
		//
		IObservableMap[] observeMaps = BeansObservables.observeMaps(listContentProvider.getKnownElements(), ObservableTrack.class, new String[]{"medium.number", "number", "title"});
		gridViewerTracks.setLabelProvider(new ObservableMapLabelProvider(observeMaps));
		//
		IObservableList getReleaseTracksObserveList = BeansObservables.observeList(Realm.getDefault(), getRelease(), "tracks");
		gridViewerTracks.setInput(getReleaseTracksObserveList);
		//
		IObservableValue gridViewerTracksObserveSingleSelection = ViewersObservables.observeDelayedValue(200, ViewersObservables.observeSingleSelection(gridViewerTracks));
		IObservableValue trackContributorPanelModelObserveValue = BeansObservables.observeValue(trackContributorPanel, "model");
		bindingContext.bindValue(gridViewerTracksObserveSingleSelection, trackContributorPanelModelObserveValue, null, new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER));
		//
		return bindingContext;
	}
}
