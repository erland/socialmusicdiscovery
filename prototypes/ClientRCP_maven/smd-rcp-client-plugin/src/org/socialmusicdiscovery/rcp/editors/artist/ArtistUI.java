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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.socialmusicdiscovery.rcp.content.ObservableArtist;
import org.socialmusicdiscovery.rcp.views.util.AbstractComposite;

public class ArtistUI extends AbstractComposite<ObservableArtist> {
	private Text textName;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	protected ScrolledForm formArtist;
	protected Label lblName;
	protected ExpandableComposite metadataReferences;
	protected Grid grid;
	private GridTableViewer gridTableViewer;
	protected GridColumn sourceCol;
	private GridViewerColumn gridViewerColumn;
	protected GridColumn valueCol;
	private GridViewerColumn valueGVC;
	protected GridItem itemSMD;
	protected GridItem itemMB;
	
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
		
		lblName = formToolkit.createLabel(formArtist.getBody(), "Name:", SWT.NONE);
		
		textName = new Text(formArtist.getBody(), SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		metadataReferences = formToolkit.createExpandableComposite(formArtist.getBody(), ExpandableComposite.TREE_NODE | ExpandableComposite.TITLE_BAR);
		GridData gd_metadataReferences = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_metadataReferences.widthHint = 194;
		metadataReferences.setLayoutData(gd_metadataReferences);
		metadataReferences.setToolTipText("Details about internal metadata and references to external sources.");
		metadataReferences.setBounds(0, 0, 166, 13);
		formToolkit.paintBordersFor(metadataReferences);
		metadataReferences.setText("Metadata References");
		metadataReferences.setExpanded(true);
		
		gridTableViewer = new GridTableViewer(metadataReferences, SWT.BORDER);
		grid = gridTableViewer.getGrid();
		grid.setHeaderVisible(true);
		formToolkit.paintBordersFor(grid);
		metadataReferences.setClient(grid);
		
		gridViewerColumn = new GridViewerColumn(gridTableViewer, SWT.NONE);
		sourceCol = gridViewerColumn.getColumn();
		sourceCol.setMoveable(true);
		sourceCol.setWidth(100);
		sourceCol.setText("Source");
		
		valueGVC = new GridViewerColumn(gridTableViewer, SWT.NONE);
		valueCol = valueGVC.getColumn();
		valueCol.setWidth(100);
		valueCol.setText("Value");
		
		itemSMD = new GridItem(grid, SWT.NONE);
		itemSMD.setText("SMD");
		
		itemMB = new GridItem(grid, SWT.NONE);
		itemMB.setText("MusicBrainz");
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
		IObservableValue textNameObserveTextObserveWidget = SWTObservables.observeText(textName, SWT.Modify);
		IObservableValue artistgetNameEmptyObserveValue = BeansObservables.observeValue(getArtist(), "name");
		bindingContext.bindValue(textNameObserveTextObserveWidget, artistgetNameEmptyObserveValue, null, null);
		//
		IObservableValue textNameObserveTooltipTextObserveWidget = SWTObservables.observeTooltipText(textName);
		IObservableValue getArtistPersonObserveValue = BeansObservables.observeValue(getArtist(), "person");
		bindingContext.bindValue(textNameObserveTooltipTextObserveWidget, getArtistPersonObserveValue, null, null);
		//
		return bindingContext;
	}
}
