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

package org.socialmusicdiscovery.rcp.editors.contributor;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.socialmusicdiscovery.rcp.content.AbstractContributableEntity;
import org.socialmusicdiscovery.rcp.content.ArtistProvider;
import org.socialmusicdiscovery.rcp.content.ContributorRoleProvider;
import org.socialmusicdiscovery.rcp.content.ObservableArtist;
import org.socialmusicdiscovery.rcp.content.ObservableContributor;
import org.socialmusicdiscovery.rcp.editors.widgets.SelectionPanel;

/**
 * Creates an {@link ObservableContributor} instance. Place on a container.
 * 
 * @author Peer Törngren
 *
 */
public class ContributorUI extends Composite {

	private Composite composite;
	private Combo roleCombo;
	private ComboViewer roleViewer;
	private Label roleLabel;
	private Label ownerLabel;
	private Text ownerText;
	private Label infoLabel;
	
	// simplify data binding in UI tool + expose observable properties to parent
	private final ObservableContributor template = new ObservableContributor(); 
	private SelectionPanel<ObservableArtist> selectionPanel;

	/**
	 * Create the dialog.
	 * @param parent
	 */
	public ContributorUI(Composite parent, int style) {
		super(parent, style);
		setBackgroundMode(SWT.INHERIT_DEFAULT);

		setLayout(new FillLayout(SWT.HORIZONTAL));
		composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		infoLabel = new Label(composite, SWT.WRAP);
		infoLabel.setText("Create a new contributor by selecting a new (unique) combination of type and artist.");
		infoLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		ownerLabel = new Label(composite, SWT.NONE);
		ownerLabel.setText("Owner:");
		ownerLabel.setToolTipText("What is this artist contributing to?");
		
		ownerText = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		ownerText.setText("");
		ownerText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		roleLabel = new Label(composite, SWT.NONE);
		roleLabel.setText("Role:");
		roleLabel.setToolTipText("How does this artist contribute?");
		
		roleViewer = new ComboViewer(composite, SWT.READ_ONLY);
		roleViewer.setSorter(new ViewerSorter());
		roleCombo = roleViewer.getCombo();
		roleCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		roleViewer.setContentProvider(new ArrayContentProvider());
		
		selectionPanel = new SelectionPanel<ObservableArtist>(composite, SWT.NONE);
		selectionPanel.getLabel().setToolTipText("Who contributes?");
		selectionPanel.getButton().setToolTipText("Select the artist that makes the contribution");
		GridLayout gridLayout = (GridLayout) selectionPanel.getLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		selectionPanel.getText().setEditable(true);
		selectionPanel.getLabel().setText("Artist:");
		selectionPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		initDataBindings();
	}

	void setArtistProvider(ArtistProvider artistProvider) {
		selectionPanel.setElementProvider(artistProvider);
	}

	void setOwner(AbstractContributableEntity owner) {
		template.setOwner(owner);
		ownerText.setText(owner.getName());
	}
	public void setType(String type) {
		template.setType(type);
	}
	
	public void setArtist(ObservableArtist artist) {
		template.setArtist(artist);
	}

	void setRoleProvider(ContributorRoleProvider contributorRoleProvider) {
		roleViewer.setInput(contributorRoleProvider.getElements());
	}

	ObservableContributor getTemplate() {
		return template;
	}
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue roleViewerObserveSingleSelection = ViewersObservables.observeSingleSelection(roleViewer);
		IObservableValue templateTypeObserveValue = BeansObservables.observeValue(template, "type");
		bindingContext.bindValue(roleViewerObserveSingleSelection, templateTypeObserveValue, null, null);
		//
		selectionPanel.bindSelection(bindingContext, template, ObservableContributor.PROP_artist);
		return bindingContext;
	}

}
