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

package org.socialmusicdiscovery.rcp.editors.artist;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.socialmusicdiscovery.rcp.content.ObservableArtist;
import org.socialmusicdiscovery.rcp.content.ObservableContributor;
import org.socialmusicdiscovery.rcp.views.util.AbstractComposite;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.socialmusicdiscovery.server.business.model.core.RecordingSession;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Work;

/**
 * Shows what entities an {@link Artist} contributes to, and in which role. 
 * Not intended as a reusable widget.
 *   
 * @author Peer Törngren
 *
 */
/* package */ class ArtistContributionsPanel extends AbstractComposite<ObservableArtist> {

	private class MyContributorFilter extends ViewerFilter{

		private Map<Class, Boolean> settings = new HashMap<Class, Boolean>();

		public void set(Class<? extends SMDIdentity> type, boolean isVisible) {
			settings.put(type, Boolean.valueOf(isVisible));
			notifyContributorPanel();
		}

		public void notifyContributorPanel() {
			contributionsPanel.setFilters(this);
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			return element instanceof ObservableContributor ? accept((ObservableContributor) element) : true; 
		}

		private boolean accept(ObservableContributor c) {
			for (Entry<Class, Boolean> entry: settings.entrySet()) {
				if (entry.getKey().isInstance(c.getOwner())) {
					return entry.getValue().booleanValue();
				}
			}
			return false;
		}
	}

	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	protected Text text;
	protected GridColumn colPerformer;
	private ContributionsPanel contributionsPanel;
	private ToolBar toolBar;
	private ToolItem recordingCheck;
	private ToolItem workCheck;
	private ToolItem albumCheck;
	private ToolItem sessionCheck;
	private Button allContributionsButton;
	private Label separator;
	private Composite rootArea;
	private Section filterSection;
	private Composite composite;
	private MyContributorFilter contributorFilter = new MyContributorFilter();
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ArtistContributionsPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		rootArea = formToolkit.createComposite(this, SWT.NONE);
		formToolkit.paintBordersFor(rootArea);
		rootArea.setLayout(new GridLayout(2, false));
		
		contributionsPanel = new ContributionsPanel(rootArea, SWT.NONE);
		contributionsPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.adapt(contributionsPanel);
		formToolkit.paintBordersFor(contributionsPanel);
		
		filterSection = formToolkit.createSection(rootArea, Section.TITLE_BAR);
		filterSection.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		formToolkit.paintBordersFor(filterSection);
		filterSection.setText("Filter");
		
		composite = formToolkit.createComposite(filterSection, SWT.NONE);
		formToolkit.paintBordersFor(composite);
		filterSection.setClient(composite);
		composite.setLayout(new GridLayout(1, false));
		
		toolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT | SWT.VERTICAL);
		formToolkit.adapt(toolBar);
		formToolkit.paintBordersFor(toolBar);
		
		albumCheck = new ToolItem(toolBar, SWT.CHECK);
		albumCheck.addSelectionListener(new AlbumCheckSelectionListener());
		albumCheck.setToolTipText("Show contributors defined for Album");
		albumCheck.setText("Album");
		
		sessionCheck = new ToolItem(toolBar, SWT.CHECK);
		sessionCheck.addSelectionListener(new SessionCheckSelectionListener());
		sessionCheck.setToolTipText("Show contributors defined for Recording Session");
		sessionCheck.setText("Session");
		
		recordingCheck = new ToolItem(toolBar, SWT.CHECK);
		recordingCheck.addSelectionListener(new RecordingCheckSelectionListener());
		recordingCheck.setToolTipText("Show contributors defined for Recording");
		recordingCheck.setText("Recording");
		
		workCheck = new ToolItem(toolBar, SWT.CHECK);
		workCheck.addSelectionListener(new WorkCheckSelectionListener());
		workCheck.setToolTipText("Show contributors defined for Work");
		workCheck.setText("Work");
		
		separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		formToolkit.adapt(separator, true, true);
		
		allContributionsButton = new Button(composite, SWT.CHECK);
		allContributionsButton.setSelection(true);
		allContributionsButton.addSelectionListener(new AllContributionsButtonSelectionListener());
		formToolkit.adapt(allContributionsButton, true, true);
		allContributionsButton.setText("All");
	
		initUI();
		}

	private void initUI() {
		contributorFilter.notifyContributorPanel();
	}

	private void updateFilters() {
		boolean isShowAll = allContributionsButton.getSelection();
		if (isShowAll) {
			contributionsPanel.setFilters();
		} else {
			contributorFilter.notifyContributorPanel();
		}
		toolBar.setEnabled(!isShowAll);
	}
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public ObservableArtist getObservableArtist() {
		return getModel();
	}
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		return bindingContext;
	}

	@Override
	protected void afterSetModel(ObservableArtist model) {
		contributionsPanel.setModel(model);
		updateFilters();
//		contributionsPanel.debug();
	}

	private class AlbumCheckSelectionListener extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			contributorFilter.set(Release.class, albumCheck.getSelection());
		}
	}
	
	private class SessionCheckSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			contributorFilter.set(RecordingSession.class, sessionCheck.getSelection());
		}
	}
	private class RecordingCheckSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			contributorFilter.set(Recording.class, recordingCheck.getSelection());
		}
	}
	private class WorkCheckSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			contributorFilter.set(Work.class, workCheck.getSelection());
		}
	}

	private class AllContributionsButtonSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			updateFilters();
		}
	}

	public GridTableViewer getGridViewer() {
		return contributionsPanel.getGridViewer();
	}
	
	public GridViewerColumn getRoleGVC() {
		return contributionsPanel.getRoleGVC();
	}
	public GridViewerColumn getEntityGVC() {
		return contributionsPanel.getEntityGVC();
	}

}
