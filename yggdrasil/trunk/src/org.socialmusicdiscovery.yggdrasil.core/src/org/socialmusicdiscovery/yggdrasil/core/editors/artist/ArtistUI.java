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

package org.socialmusicdiscovery.yggdrasil.core.editors.artist;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
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
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableArtist;
import org.socialmusicdiscovery.yggdrasil.foundation.views.util.AbstractComposite;

public class ArtistUI extends AbstractComposite<ObservableArtist> {
	private Text nameText;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	protected ScrolledForm formArtist;
	protected Label nameLabel;
	private Section contributionSection;
	private Section artistDataSection;
	private Composite composite;
	private CTabFolder artistDataTabFolder;
	private CTabItem aliasTab;
	private CTabItem memberTab;
	private ArtistContributionsPanel artistContributionsPanel;
	private Composite memberArea;
	private Composite aliasArea;
	private Label memberLabel;
	private Label aliasLabel;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ArtistUI(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		formArtist = formToolkit.createScrolledForm(this);
		formArtist.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(formArtist);
		formArtist.setText("Artist");
		formArtist.getBody().setLayout(new GridLayout(1, false));
		
		nameLabel = formToolkit.createLabel(formArtist.getBody(), "Name:", SWT.NONE);
		
		nameText = new Text(formArtist.getBody(), SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		contributionSection = formToolkit.createSection(formArtist.getBody(), Section.TITLE_BAR);
		contributionSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(contributionSection);
		contributionSection.setText("Contributions");
		
		
		composite = formToolkit.createComposite(contributionSection, SWT.NONE);
		formToolkit.paintBordersFor(composite);
		contributionSection.setClient(composite);
		composite.setLayout(new GridLayout(1, false));
		
		artistContributionsPanel = new ArtistContributionsPanel(composite, SWT.NONE);
		artistContributionsPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.adapt(artistContributionsPanel);
		formToolkit.paintBordersFor(artistContributionsPanel);
			
		artistDataSection = formToolkit.createSection(formArtist.getBody(), Section.TWISTIE | Section.TITLE_BAR);
		artistDataSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		formToolkit.paintBordersFor(artistDataSection);
		artistDataSection.setText("Artist Data");
		
		artistDataTabFolder = new CTabFolder(artistDataSection, SWT.BORDER | SWT.BOTTOM);
		formToolkit.adapt(artistDataTabFolder);
		formToolkit.paintBordersFor(artistDataTabFolder);
		artistDataSection.setClient(artistDataTabFolder);
		artistDataTabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		memberTab = new CTabItem(artistDataTabFolder, SWT.NONE);
		memberTab.setText("Member/Members");
		
		memberArea = formToolkit.createComposite(artistDataTabFolder, SWT.NONE);
		memberTab.setControl(memberArea);
		formToolkit.paintBordersFor(memberArea);
		memberArea.setLayout(new GridLayout(1, false));
		
		memberLabel = new Label(memberArea, SWT.NONE);
		formToolkit.adapt(memberLabel, true, true);
		memberLabel.setText("(artist members and memberships to be edited here)");
		
		aliasTab = new CTabItem(artistDataTabFolder, SWT.NONE);
		aliasTab.setText("Aliases");
		
		aliasArea = formToolkit.createComposite(artistDataTabFolder, SWT.NONE);
		aliasTab.setControl(aliasArea);
		formToolkit.paintBordersFor(aliasArea);
		aliasArea.setLayout(new GridLayout(1, false));
		
		aliasLabel = new Label(aliasArea, SWT.NONE);
		formToolkit.adapt(aliasLabel, true, true);
		aliasLabel.setText("(artist aliases to be edited here)");
		
		initUI();
	}

	private void initUI() {
		artistDataTabFolder.setSelection(memberTab);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @return {@link ObservableArtist}
	 * @see #getModel()
	 */
	public ObservableArtist getArtist() {
		return getModel();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue textNameObserveTextObserveWidget = SWTObservables.observeText(nameText, SWT.Modify);
		IObservableValue artistgetNameEmptyObserveValue = BeansObservables.observeValue(getArtist(), "name");
		bindingContext.bindValue(textNameObserveTextObserveWidget, artistgetNameEmptyObserveValue, null, null);
		//
		IObservableValue textNameObserveTooltipTextObserveWidget = SWTObservables.observeTooltipText(nameText);
		IObservableValue getArtistPersonObserveValue = BeansObservables.observeValue(getArtist(), "person");
		bindingContext.bindValue(textNameObserveTooltipTextObserveWidget, getArtistPersonObserveValue, null, null);
		//
		return bindingContext;
	}
	public ArtistContributionsPanel getArtistContributionsPanel() {
		return artistContributionsPanel;
	}
	public Composite getMemberArea() {
		return memberArea;
	}
	public Composite getAliasArea() {
		return aliasArea;
	}

	@Override
	protected void afterSetModel(ObservableArtist model) {
		super.afterSetModel(model);
		getArtistContributionsPanel().setModel(model);
	}
}
