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
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.socialmusicdiscovery.rcp.content.ObservablePlayableElement;
import org.socialmusicdiscovery.rcp.content.ObservableTrack;
import org.socialmusicdiscovery.rcp.util.ViewerUtil;
import org.socialmusicdiscovery.rcp.views.util.AbstractComposite;

/**
 * @author Peer Törngren
 *
 */
public class PlayableElementsPanel extends AbstractComposite<ObservableTrack> {

	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Grid grid;
	private GridTableViewer gridTableViewer;
	private GridColumn uriColumn;
	private GridViewerColumn uriGVC;
	private GridColumn formatColumn;
	private GridViewerColumn formatGVC;
	private GridColumn bitrateColumn;
	private GridViewerColumn bitrateGVC;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PlayableElementsPanel(Composite parent, int style) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		gridTableViewer = new GridTableViewer(this, SWT.BORDER);
		grid = gridTableViewer.getGrid();
		grid.setHeaderVisible(true);
		toolkit.paintBordersFor(grid);
		
		uriGVC = new GridViewerColumn(gridTableViewer, SWT.NONE);
		uriColumn = uriGVC.getColumn();
		uriColumn.setWidth(300);
		uriColumn.setText("Location (URI)");
		
		formatGVC = new GridViewerColumn(gridTableViewer, SWT.NONE);
		formatColumn = formatGVC.getColumn();
		formatColumn.setWidth(75);
		formatColumn.setText("Format");
		
		bitrateGVC = new GridViewerColumn(gridTableViewer, SWT.NONE);
		bitrateColumn = bitrateGVC.getColumn();
		bitrateColumn.setWidth(75);
		bitrateColumn.setText("Bitrate");

	}
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		//
		return bindingContext;
	}

	@Override
	protected void afterSetModel(ObservableTrack model) {
		super.afterSetModel(model);
		IBeanValueProperty uriProperty = BeanProperties.value(ObservablePlayableElement.class, "uri");
		IBeanValueProperty formatProperty = BeanProperties.value(ObservablePlayableElement.class, "format");
		IBeanValueProperty bitrateProperty = BeanProperties.value(ObservablePlayableElement.class, "bitrate");
		IObservableSet set = new WritableSet(getModel().getPlayableElements(), ObservablePlayableElement.class);
		ViewerUtil.bind(gridTableViewer, set, uriProperty, formatProperty, bitrateProperty);
	}

	public GridTableViewer getGridTableViewer() {
		return gridTableViewer;
	}
}
