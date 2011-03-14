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

package org.socialmusicdiscovery.rcp.editors.recording;

import java.util.Comparator;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.socialmusicdiscovery.rcp.content.ObservableRecording;
import org.socialmusicdiscovery.rcp.content.ObservableTrack;
import org.socialmusicdiscovery.rcp.content.ObservableWork;
import org.socialmusicdiscovery.rcp.editors.widgets.ContributorPanel;
import org.socialmusicdiscovery.rcp.util.Util;
import org.socialmusicdiscovery.rcp.util.ViewerUtil;
import org.socialmusicdiscovery.rcp.views.util.AbstractComposite;
import org.socialmusicdiscovery.rcp.views.util.OpenListener;
import org.socialmusicdiscovery.server.business.model.core.Medium;
import org.socialmusicdiscovery.server.business.model.core.Track;

public class RecordingUI extends AbstractComposite<ObservableRecording> {

	private final class MyTrackNumberComparator implements Comparator<Track> {
		@Override
		public int compare(Track t1, Track t2) {
			return Util.compare(t1.getNumber(), t2.getNumber());
		}
	}
	
	private class MyTrackMediumNumberComparator implements Comparator<Track> {
		@Override
		public int compare(Track t1, Track t2) {
			return Util.compare(getNumber(t1), getNumber(t2));
		}

		private Integer getNumber(Track t) {
			Medium m = t.getMedium();
			return m==null ? null : m.getNumber();
			
		}
	}

	private Text nameText;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private ScrolledForm scrldfrmRecording;
	private ContributorPanel artistPanel;
	private Label nameLabel;
	private Section sctnRecordingData;
	private Grid tracksGrid;
	private GridTableViewer tracksViewer;
	private GridColumn releaseColumn;
	private GridViewerColumn releaseGVC;
	private GridColumn mediumNumberColumn;
	private GridViewerColumn mediumNumberGVC;
	private GridColumn trackNumberColumn;
	private GridViewerColumn trackNumberGVC;
	private Label derivedNameLabel;
	private Text derivedName;
	private CTabFolder tabFolder;
	private CTabItem artistTab;
	private CTabItem workTab;
	private Grid workGrid;
	private GridTableViewer worksViewer;
	private GridColumn workColumn;
	private GridViewerColumn workGVC;
	private CTabItem sessionTab;
	private Link sessionLink;
	private Composite sessionComposite;
	private ContributorPanel sessionArtistPanel;
	private Text sessionText;
	private Label lblWarningThis;
	private Section sctnTracks;
	private Composite tracksArea;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public RecordingUI(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		scrldfrmRecording = formToolkit.createScrolledForm(this);
		scrldfrmRecording.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(scrldfrmRecording);
		scrldfrmRecording.setText("Recording");
		scrldfrmRecording.getBody().setLayout(new GridLayout(1, false));
		
		lblWarningThis = new Label(scrldfrmRecording.getBody(), SWT.CENTER);
		lblWarningThis.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		formToolkit.adapt(lblWarningThis, true, true);
		lblWarningThis.setText("WARNING - THIS IS WORK IN PROGRESS! ONLY FRAGMENTS WORK!");
		
		nameLabel = new Label(scrldfrmRecording.getBody(), SWT.NONE);
		formToolkit.adapt(nameLabel, true, true);
		nameLabel.setText("Name");
		
		nameText = formToolkit.createText(scrldfrmRecording.getBody(), "text", SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		nameText.setText("");
		
		derivedNameLabel = new Label(scrldfrmRecording.getBody(), SWT.NONE);
		formToolkit.adapt(derivedNameLabel, true, true);
		derivedNameLabel.setText("Default Name (derived from work)");
		
		derivedName = new Text(scrldfrmRecording.getBody(), SWT.BORDER | SWT.READ_ONLY);
		derivedName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(derivedName, true, true);
		
		sctnTracks = formToolkit.createSection(scrldfrmRecording.getBody(), Section.EXPANDED | Section.TITLE_BAR);
		GridData gd_sctnTracks = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_sctnTracks.heightHint = 96;
		sctnTracks.setLayoutData(gd_sctnTracks);
		formToolkit.paintBordersFor(sctnTracks);
		sctnTracks.setText("Tracks");
		
		tracksArea = formToolkit.createComposite(sctnTracks, SWT.NONE);
		formToolkit.paintBordersFor(tracksArea);
		sctnTracks.setClient(tracksArea);
		tracksArea.setLayout(new FillLayout(SWT.VERTICAL));
		
		tracksViewer = new GridTableViewer(tracksArea, SWT.BORDER | SWT.V_SCROLL);
		tracksGrid = tracksViewer.getGrid();
		tracksGrid.setCellSelectionEnabled(true);
		tracksGrid.setHeaderVisible(true);
		formToolkit.paintBordersFor(tracksGrid);
		
		releaseGVC = new GridViewerColumn(tracksViewer, SWT.NONE);
		releaseColumn = releaseGVC.getColumn();
		releaseColumn.setMoveable(true);
		releaseColumn.setWidth(200);
		releaseColumn.setText("Release");
		
		mediumNumberGVC = new GridViewerColumn(tracksViewer, SWT.NONE);
		mediumNumberColumn = mediumNumberGVC.getColumn();
		mediumNumberColumn.setMoveable(true);
		mediumNumberColumn.setWidth(50);
		mediumNumberColumn.setText("Medium #");
		
		trackNumberGVC = new GridViewerColumn(tracksViewer, SWT.NONE);
		trackNumberColumn = trackNumberGVC.getColumn();
		trackNumberColumn.setMoveable(true);
		trackNumberColumn.setWidth(50);
		trackNumberColumn.setText("Track #");
		
		sctnRecordingData = formToolkit.createSection(scrldfrmRecording.getBody(), Section.TWISTIE | Section.TITLE_BAR);
		sctnRecordingData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(sctnRecordingData);
		sctnRecordingData.setText("Recording Data");
		sctnRecordingData.setExpanded(true);
		
		tabFolder = new CTabFolder(sctnRecordingData, SWT.BORDER | SWT.FLAT | SWT.BOTTOM);
		formToolkit.adapt(tabFolder);
		formToolkit.paintBordersFor(tabFolder);
		sctnRecordingData.setClient(tabFolder);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		artistTab = new CTabItem(tabFolder, SWT.NONE);
		artistTab.setText("Artist(s)");
		
		artistPanel = new ContributorPanel(tabFolder, true);
		artistTab.setControl(artistPanel);
		formToolkit.paintBordersFor(artistPanel);
		
		workTab = new CTabItem(tabFolder, SWT.NONE);
		workTab.setText("Work");
		
		worksViewer = new GridTableViewer(tabFolder, SWT.BORDER);
		workGrid = worksViewer.getGrid();
		workGrid.setHeaderVisible(true);
		workTab.setControl(workGrid);
		formToolkit.paintBordersFor(workGrid);
		
		workGVC = new GridViewerColumn(worksViewer, SWT.NONE);
		workColumn = workGVC.getColumn();
		workColumn.setWidth(400);
		workColumn.setText("Work");
		
		sessionTab = new CTabItem(tabFolder, SWT.NONE);
		sessionTab.setText("Session");
		
		sessionComposite = new Composite(tabFolder, SWT.NONE);
		sessionTab.setControl(sessionComposite);
		formToolkit.paintBordersFor(sessionComposite);
		sessionComposite.setLayout(new GridLayout(1, false));
		
		sessionLink = new Link(sessionComposite, SWT.NONE);
		sessionLink.setText("<a>Recording Session</a> (optional)");
		
		sessionText = new Text(sessionComposite, SWT.BORDER | SWT.READ_ONLY);
		sessionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(sessionText, true, true);
		
		sessionArtistPanel = new ContributorPanel(sessionComposite, SWT.NONE);
		
		hookListeners();
		}

	private void hookListeners() {
		// default edit
		tracksViewer.addOpenListener(new OpenListener());
		ViewerUtil.hookSorter(releaseGVC, workGVC);
		ViewerUtil.hookSorter(new MyTrackMediumNumberComparator(),  mediumNumberGVC);
		ViewerUtil.hookSorter(new MyTrackNumberComparator(),  trackNumberGVC);
	}
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void afterSetModel(ObservableRecording track) {
		getArtistPanel().bindContributors(getModel().getContributors());
		bindTracks();
		bindWorks();
//		bindSessionPanel();
	}
	
	private void bindWorks() {
		WritableList list = new WritableList(getModel().getWorks(), ObservableWork.class);
		IBeanValueProperty name = BeanProperties.value(ObservableWork.class, "name");
		ViewerUtil.bind(worksViewer, list, name);
	}

	private void bindTracks() {
		WritableList list = new WritableList(getModel().getTracks(), ObservableTrack.class);
		IBeanValueProperty medium = BeanProperties.value(ObservableTrack.class, "medium.number");
		IBeanValueProperty track = BeanProperties.value(ObservableTrack.class, "number");
		IBeanValueProperty release = BeanProperties.value(ObservableTrack.class, "release.name");
		ViewerUtil.bind(tracksViewer, list, release, medium, track);
	}

	public ContributorPanel getArtistPanel() {
		return artistPanel;
	}
	
	/**
	 * @return {@link ObservableRecording}
	 * @see #getModel()
	 */
	public ObservableRecording getRecording() {
		return getModel();
	}
	public Link getSessionLink() {
		return sessionLink;
	}
	public Text getSessionText() {
		return sessionText;
	}
	public ContributorPanel getSessionArtistPanel() {
		return sessionArtistPanel;
	}
	public GridTableViewer getWorkGridViewer() {
		return worksViewer;
	}
	public GridViewerColumn getWorkGVC() {
		return workGVC;
	}
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue nameTextObserveTextObserveWidget = SWTObservables.observeText(nameText, SWT.Modify);
		IObservableValue getRecordingNameObserveValue = BeansObservables.observeValue(getRecording(), "name");
		bindingContext.bindValue(nameTextObserveTextObserveWidget, getRecordingNameObserveValue, null, null);
		//
		IObservableValue derivedNameObserveTextObserveWidget = SWTObservables.observeText(derivedName, SWT.Modify);
		IObservableValue getRecordingDerivedNameObserveValue = BeansObservables.observeValue(getRecording(), "derivedName");
		bindingContext.bindValue(derivedNameObserveTextObserveWidget, getRecordingDerivedNameObserveValue, null, null);
		//
		IObservableValue derivedNameLabelObserveEnabledObserveWidget = SWTObservables.observeEnabled(derivedNameLabel);
		IObservableValue getRecordingDerivedNameUsedObserveValue = BeansObservables.observeValue(getRecording(), "derivedNameUsed");
		bindingContext.bindValue(derivedNameLabelObserveEnabledObserveWidget, getRecordingDerivedNameUsedObserveValue, null, null);
		//
		IObservableValue derivedNameObserveEnabledObserveWidget = SWTObservables.observeEnabled(derivedName);
		bindingContext.bindValue(derivedNameObserveEnabledObserveWidget, getRecordingDerivedNameUsedObserveValue, null, null);
		//
		return bindingContext;
	}
	public GridTableViewer getTracksViewer() {
		return tracksViewer;
	}
}
