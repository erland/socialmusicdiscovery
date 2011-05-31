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

package org.socialmusicdiscovery.rcp.dialogs;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ListDialog;
import org.socialmusicdiscovery.rcp.content.AbstractContributableEntity;
import org.socialmusicdiscovery.rcp.content.ArtistProvider;
import org.socialmusicdiscovery.rcp.content.ContributorRoleProvider;
import org.socialmusicdiscovery.rcp.content.ObservableContributor;
import org.socialmusicdiscovery.rcp.views.util.LabelProviderFactory;
import org.socialmusicdiscovery.server.business.model.core.Artist;

/**
 * Creates an {@link ObservableContributor} instance. Place on a container.
 * 
 * @author Peer Törngren
 *
 */
public class ContributorFactoryUI extends Composite {

	private Composite composite;
	private Combo roleCombo;
	private ComboViewer roleViewer;
	private Label roleLabel;
	private Label artistLabel;
	private Composite artistArea;
	private Button artistButton;
	private StyledText artistText;
	private ArtistProvider artistProvider;
	private ContributorRoleProvider roleProvider;
	private Label ownerLabel;
	private Text ownerText;
	private Label infoLabel;
	
	// simplify data binding in UI tool + expose observable properties to parent
	private final ObservableContributor template = new ObservableContributor(); 

	/**
	 * Create the dialog.
	 * @param parent
	 */
	public ContributorFactoryUI(Composite parent, int style) {
		super(parent, style);
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
		
		artistLabel = new Label(composite, SWT.NONE);
		artistLabel.setText("Artist:");
		artistLabel.setToolTipText("Who is contributing?");
		artistLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		
		artistArea = new Composite(composite, SWT.NONE);
		artistArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		GridLayout gl_artistArea = new GridLayout(2, false);
		gl_artistArea.verticalSpacing = 0;
		gl_artistArea.horizontalSpacing = 0;
		gl_artistArea.marginHeight = 0;
		gl_artistArea.marginWidth = 0;
		artistArea.setLayout(gl_artistArea);
		
		artistText = new StyledText(artistArea, SWT.BORDER | SWT.READ_ONLY);
		artistText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		artistButton = new Button(artistArea, SWT.NONE);
		artistButton.setText("...");
		artistButton.addSelectionListener(new ArtistButtonSelectionListener());
		artistButton.setToolTipText("Select artist from a selector dialog.");

		initDataBindings();
	}

	void setArtistProvider(ArtistProvider artistProvider) {
		this.artistProvider = artistProvider;
	}

	void setOwner(AbstractContributableEntity owner) {
		ownerText.setText(owner.getName());
	}

	void setRoleProvider(ContributorRoleProvider contributorRoleProvider) {
		this.roleProvider = contributorRoleProvider;
		roleViewer.setInput(roleProvider.getElements());
	}

	Artist getArtist() {
		return template.getArtist();
	}

	String getType() {
		return template.getType();
	}
	
	private class ArtistButtonSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			ListDialog ld = new ListDialog(getShell());
			ld.setContentProvider(new ArrayContentProvider());
			ld.setInput(artistProvider.getElements());
			ld.setLabelProvider(LabelProviderFactory.defaultStatic());
			ld.setMessage("Choose artist");
			ld.setTitle("Choose");
			ld.open();
			
			Object[] allSelected = ld.getResult();
			Object selected = allSelected!=null && allSelected.length==1 ? allSelected[0] : null;
			Artist artist = (Artist) selected;
			template.setArtist(artist);
			artistText.setText(artist.getName());
		}
	}

	public ObservableContributor getTemplate() {
		return template;
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue roleViewerObserveSingleSelection = ViewersObservables.observeSingleSelection(roleViewer);
		IObservableValue templateTypeObserveValue = BeansObservables.observeValue(template, "type");
		bindingContext.bindValue(roleViewerObserveSingleSelection, templateTypeObserveValue, null, null);
		//
		return bindingContext;
	}
}
