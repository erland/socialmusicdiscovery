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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
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
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.socialmusicdiscovery.yggdrasil.core.editors.ContributorPanel;
import org.socialmusicdiscovery.yggdrasil.core.editors.TrackMediumNumberComparator;
import org.socialmusicdiscovery.yggdrasil.core.editors.TrackNumberComparator;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableLabel;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableRelease;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableTrack;
import org.socialmusicdiscovery.yggdrasil.foundation.content.RecordLabelProvider;
import org.socialmusicdiscovery.yggdrasil.foundation.util.ViewerUtil;
import org.socialmusicdiscovery.yggdrasil.foundation.util.databinding.DateConverter;
import org.socialmusicdiscovery.yggdrasil.foundation.util.databinding.StringToYearValidator;
import org.socialmusicdiscovery.yggdrasil.foundation.views.util.AbstractComposite;
import org.socialmusicdiscovery.yggdrasil.foundation.views.util.OpenListener;

public class ReleaseUI extends AbstractComposite<ObservableRelease> {

	private Text textName;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	protected ScrolledForm formRelease;
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
	private Section sectionTracks;
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
	private Composite tracksArea;
	private TrackContributorPanel trackContributorPanel;
	private Composite composite;
	private CTabFolder trackDetailsFolder;
	private CTabItem trackContributorTab;
	private Section trackDetailsSection;
	private CTabItem trackPlayableTab;
	private PlayableElementsPanel playableElementsPanel;
	private DetailsPanel detailsPanel;

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
		
		formRelease = formToolkit.createScrolledForm(this);
		GridData gd_scrldfrmRelease = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_scrldfrmRelease.widthHint = 1143;
		formRelease.setLayoutData(gd_scrldfrmRelease);
		formToolkit.paintBordersFor(formRelease);
		formRelease.setText("Release");
		formRelease.getBody().setLayout(new GridLayout(1, false));
		
		Label lblName = formToolkit.createLabel(formRelease.getBody(), "Name", SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		textName = formToolkit.createText(formRelease.getBody(), "text", SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textName.setText("");
		
		sectionTracks = formToolkit.createSection(formRelease.getBody(), Section.EXPANDED | Section.TITLE_BAR);
		sectionTracks.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(sectionTracks);
		sectionTracks.setText("Tracks");
		
		gridContainer = formToolkit.createComposite(sectionTracks, SWT.NONE);
		formToolkit.paintBordersFor(gridContainer);
		sectionTracks.setClient(gridContainer);
		FillLayout fl_gridContainer = new FillLayout(SWT.HORIZONTAL);
		fl_gridContainer.marginWidth = 5;
		fl_gridContainer.spacing = 5;
		gridContainer.setLayout(fl_gridContainer);
		
		sashForm = new SashForm(gridContainer, SWT.SMOOTH);
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
		
		trackDetailsSection = formToolkit.createSection(sashForm, Section.SHORT_TITLE_BAR);
		formToolkit.paintBordersFor(trackDetailsSection);
		trackDetailsSection.setText("Track Details");
		trackDetailsSection.setExpanded(true);
		
		trackDetailsFolder = new CTabFolder(trackDetailsSection, SWT.BORDER | SWT.BOTTOM);
		trackDetailsSection.setClient(trackDetailsFolder);
		formToolkit.adapt(trackDetailsFolder);
		formToolkit.paintBordersFor(trackDetailsFolder);
		trackDetailsFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		trackContributorTab = new CTabItem(trackDetailsFolder, SWT.NONE);
		trackContributorTab.setText("Artists");
		
		trackContributorPanel = new TrackContributorPanel(trackDetailsFolder, SWT.NONE);
		trackContributorTab.setControl(trackContributorPanel);
		formToolkit.adapt(trackContributorPanel);
		formToolkit.paintBordersFor(trackContributorPanel);
		
		trackPlayableTab = new CTabItem(trackDetailsFolder, SWT.NONE);
		trackPlayableTab.setText("Sounds");
		
		playableElementsPanel = new PlayableElementsPanel(trackDetailsFolder, SWT.NONE);
		trackPlayableTab.setControl(playableElementsPanel);
		formToolkit.paintBordersFor(playableElementsPanel);
		sashForm.setWeights(new int[] {404, 442});
		
		
		sectionAlbumData = formToolkit.createSection(formRelease.getBody(), Section.TWISTIE | Section.TITLE_BAR);
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
		
		detailsPanel = new DetailsPanel(albumDataTabFolder, SWT.NONE);
		detailsPanel.getSelectionPanel().getText().setEditable(false);
		itemDetails.setControl(detailsPanel);
		formToolkit.adapt(detailsPanel);
		formToolkit.paintBordersFor(detailsPanel);

		initUI();
		}

	private void initUI() {
		trackDetailsFolder.setSelection(trackContributorTab);
		albumDataTabFolder.setSelection	(contributorTab);
		gridViewerTracks.addOpenListener(new OpenListener()); // default edit (double-click)
		
		ViewerUtil.hookSorter(gvcTitle);
		ViewerUtil.hookSorter(new TrackMediumNumberComparator(),  gvcMediumNbr);
		ViewerUtil.hookSorter(new TrackNumberComparator(),  gvcTrackNumber);
		
		detailsPanel.getSelectionPanel().setElementProvider(new RecordLabelProvider());

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
	public PlayableElementsPanel getPlayableElementsPanel() {
		return playableElementsPanel;
	}
	public TrackContributorPanel getTrackContributorPanel() {
		return trackContributorPanel;
	}
	public DetailsPanel getDetailsPanel() {
		return detailsPanel;
	}

	protected void initManualDataBindings(DataBindingContext bindingContext) {
		getDetailsPanel().getSelectionPanel().bindSelection(bindingContext, getModel(), ObservableRelease.PROP_label, ObservableLabel.PROP_name);
		
		bindDateToYear(bindingContext, detailsPanel.getYearText(), "date");
	}

	private void bindDateToYear(DataBindingContext bindingContext, Text text, String propertyName) {
		IObservableValue textValue = SWTObservables.observeText(text, SWT.Modify);
		UpdateValueStrategy dateToTextStrategy = new UpdateValueStrategy();
		dateToTextStrategy.setConverter(DateConverter.dateToYear());
		
		IObservableValue dateValue = PojoObservables.observeValue(getModel(), propertyName);
		UpdateValueStrategy textToDateStrategy = new UpdateValueStrategy();
		textToDateStrategy.setAfterGetValidator(new StringToYearValidator());
		textToDateStrategy.setConverter(DateConverter.yearToDate());

		ValidationStatusProvider binding = bindingContext.bindValue(textValue, dateValue, textToDateStrategy, dateToTextStrategy);
		
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);
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
		gridViewerTracks.setInput(getRelease().getTracks());
		//

		//
		IObservableValue gridViewerTracksObserveSingleSelection = ViewersObservables.observeDelayedValue(200, ViewersObservables.observeSingleSelection(gridViewerTracks));
		IObservableValue trackContributorPanelModelObserveValue = BeansObservables.observeValue(trackContributorPanel, "model");
		bindingContext.bindValue(gridViewerTracksObserveSingleSelection, trackContributorPanelModelObserveValue, null, new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER));
		//
		IObservableValue gridViewerTracksObserveSingleSelection_1 = ViewersObservables.observeSingleSelection(gridViewerTracks);
		IObservableValue playableElementsPanelModelObserveValue = BeansObservables.observeValue(playableElementsPanel, "model");
		bindingContext.bindValue(gridViewerTracksObserveSingleSelection_1, playableElementsPanelModelObserveValue, null, new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER));
		//
		return bindingContext;
	}
}
