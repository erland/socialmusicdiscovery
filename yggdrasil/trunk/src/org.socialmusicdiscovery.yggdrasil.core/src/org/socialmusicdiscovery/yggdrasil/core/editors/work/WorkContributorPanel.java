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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.yggdrasil.core.editors.ContributorPanel;
import org.socialmusicdiscovery.yggdrasil.foundation.content.AbstractObservableEntity;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableContributor;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableTrack;
import org.socialmusicdiscovery.yggdrasil.foundation.views.util.AbstractComposite;

public class WorkContributorPanel extends AbstractComposite<ObservableTrack> {

	private class MyEffectiveContributorsFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			return element instanceof ObservableContributor ? accept((ObservableContributor) element) : true; 
		}

		private boolean accept(ObservableContributor c) {
			return getModel().isEffectiveContributor(c);
		}

	}
	
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	protected Text text;
	protected GridColumn colPerformer;
	private ContributorPanel contributorPanel;
	private ToolBar toolBar;
	private Button effectiveContributorsButton;
	private Composite rootArea;
	private Section filterSection;
	private Composite composite;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public WorkContributorPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		rootArea = formToolkit.createComposite(this, SWT.NONE);
		formToolkit.paintBordersFor(rootArea);
		GridLayout gl_rootArea = new GridLayout(2, false);
		gl_rootArea.marginHeight = 0;
		gl_rootArea.marginWidth = 0;
		rootArea.setLayout(gl_rootArea);
		
		contributorPanel = new ContributorPanel(rootArea, SWT.NONE);
		contributorPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.adapt(contributorPanel);
		formToolkit.paintBordersFor(contributorPanel);
		
		filterSection = formToolkit.createSection(rootArea, Section.TITLE_BAR);
		filterSection.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
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
		
		effectiveContributorsButton = new Button(composite, SWT.CHECK);
		effectiveContributorsButton.setSelection(true);
		effectiveContributorsButton.addSelectionListener(new EffectiveContributorsButtonSelectionListener());
		formToolkit.adapt(effectiveContributorsButton, true, true);
		effectiveContributorsButton.setText("Effective");
	
		}

	private void updateFilters() {
		boolean isShowEffective = effectiveContributorsButton.getSelection();
		if (isShowEffective) {
			contributorPanel.setFilters(new MyEffectiveContributorsFilter());
		} else {
			contributorPanel.setFilters();
		}
		toolBar.setEnabled(!isShowEffective);
	}
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public AbstractObservableEntity<Track> getObservableTrack() {
		return getModel();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		return bindingContext;
	}

	@Override
	protected void afterSetModel(ObservableTrack model) {
		contributorPanel.setModel(model.getContributionFacade());
		updateFilters();
//		contributorPanel.debug();
	}

	private class EffectiveContributorsButtonSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			updateFilters();
		}
	}
	public ContributorPanel getContributorPanel() {
		return contributorPanel;
	}
}
