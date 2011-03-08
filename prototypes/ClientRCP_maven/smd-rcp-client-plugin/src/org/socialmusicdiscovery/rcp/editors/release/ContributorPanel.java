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

import org.eclipse.jface.viewers.ArrayContentProvider;
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
import org.socialmusicdiscovery.rcp.util.ViewerUtil;
import org.socialmusicdiscovery.rcp.views.util.OpenListener;

/**
 * A reusable grid for any type of contributor.
 * 
 * @author Peer Törngren
 *
 */
public class ContributorPanel extends Composite {

	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private GridTableViewer gridTableViewer;
	private GridColumn roleColumn;
	private GridViewerColumn roleGVC;
	private GridColumn artistColumn;
	private GridViewerColumn artistGVC;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ContributorPanel(Composite parent, int style) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		gridTableViewer = new GridTableViewer(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		Grid grid = gridTableViewer.getGrid();
		grid.setRowHeaderVisible(true);
		grid.setHeaderVisible(true);
		grid.setCellSelectionEnabled(true);
		gridTableViewer.setContentProvider(new ArrayContentProvider());
		toolkit.paintBordersFor(grid);
		
		roleGVC = new GridViewerColumn(gridTableViewer, SWT.NONE);
		roleColumn = roleGVC.getColumn();
		roleColumn.setMoveable(true);
		roleColumn.setWidth(100);
		roleColumn.setText("Role");
		
		artistGVC = new GridViewerColumn(gridTableViewer, SWT.NONE);
		artistColumn = artistGVC.getColumn();
		artistColumn.setMoveable(true);
		artistColumn.setWidth(400);
		artistColumn.setText("Artist");
		
		hookListeners();
	}
	
	private void hookListeners() {
		// default edit
		gridTableViewer.addOpenListener(new OpenListener());
		ViewerUtil.hookSorter(roleGVC, artistGVC);
	}
	
	public GridTableViewer getGridViewer() {
		return gridTableViewer;
	}

	public GridViewerColumn getRoleGVC() {
		return roleGVC;
	}
	public GridViewerColumn getArtistGVC() {
		return artistGVC;
	}
}
