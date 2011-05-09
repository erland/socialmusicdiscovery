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

package org.socialmusicdiscovery.rcp.editors.widgets;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;
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
import org.socialmusicdiscovery.rcp.content.AbstractContributableEntity;
import org.socialmusicdiscovery.rcp.content.ObservableContributor;
import org.socialmusicdiscovery.rcp.grid.GridTableColumnLayout;
import org.socialmusicdiscovery.rcp.util.Debug;
import org.socialmusicdiscovery.rcp.util.ViewerUtil;
import org.socialmusicdiscovery.rcp.views.util.AbstractComposite;
import org.socialmusicdiscovery.rcp.views.util.OpenListener;
import org.socialmusicdiscovery.server.business.model.core.Contributor;

/**
 * A reusable grid composite for maintaining any type of contributors in any
 * type of context. Caller must supply a list of {@link Contributor}s to use as
 * input (see {@link #bindContributors(Set)}).
 * 
 * @author Peer Törngren
 * 
 */
public class ContributorPanel extends AbstractComposite<AbstractContributableEntity> {

	private static final String PROP_filters = "filters";
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private GridTableViewer gridTableViewer;
	private GridColumn roleColumn;
	private GridViewerColumn roleGVC;
	private GridColumn artistColumn;
	private GridViewerColumn artistGVC;
	private ViewerFilter[] filters = new ViewerFilter[0];

	/**
	 * Create the composite with optional {@link TableColumnLayout}. Separate
	 * constructor since WindowBuilder doesn't appear to like this.
	 * 
	 * @param parent
	 * @param isAdjustColumnLayout
	 */
	public ContributorPanel(Composite parent, boolean isAdjustColumnLayout) {
		this(parent, SWT.NONE);
		if (isAdjustColumnLayout) {
			GridTableColumnLayout gridTableColumnLayout = new GridTableColumnLayout();
			gridTableColumnLayout.computeWeights(gridTableViewer.getGrid());
			setLayout(gridTableColumnLayout);
		}
	}
	
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
		setLayout(new FillLayout());
		
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
		roleColumn.setResizeable(true);
		roleColumn.setWidth(100);
		roleColumn.setText("Role");
		
		artistGVC = new GridViewerColumn(gridTableViewer, SWT.NONE);
		artistColumn = artistGVC.getColumn();
		artistColumn.setMoveable(true);
		artistColumn.setResizeable(true);
		artistColumn.setWidth(400);
		artistColumn.setText("Artist");
		
		hookListeners();
	}
	
	private void hookListeners() {
		ViewerUtil.hookSorter(roleGVC, artistGVC);
		// default edit
		gridTableViewer.addOpenListener(new OpenListener());
	}

	public GridTableViewer getGridViewer() {
		return gridTableViewer;
	}

	@Override
	protected void afterSetModel(AbstractContributableEntity model) {
		IBeanValueProperty roleProperty = BeanProperties.value(ObservableContributor.class, "type");
		IBeanValueProperty artistProperty = BeanProperties.value(ObservableContributor.class, "artist.name");
		IObservableSet set = getModel().getContributors();
		ViewerUtil.bind(gridTableViewer, set, roleProperty, artistProperty);
	}

	public ViewerFilter[] getFilters() {
		return filters;
	}

	public void setFilters(ViewerFilter... filters) {
		gridTableViewer.setFilters(filters);
//		debug(gridTableViewer);
		firePropertyChange(PROP_filters, this.filters, this.filters = filters);
	}

	@SuppressWarnings("unused")
	private void debug(StructuredViewer v) {
		if (getModel()!=null) {
			ViewerFilter[] viewerFilters = v.getFilters();
			Set contributors = getModel().getContributors();
			Debug.debug(this, "Track", getModel());
			Debug.debug(this, "All", contributors);
			Debug.debug(this, "Filters", (Object[]) viewerFilters);
			Debug.debug(this, "Filtered result", filter(v, viewerFilters, contributors));
		}
	}

	@SuppressWarnings("unchecked")
	private static Object[] filter(StructuredViewer v, ViewerFilter[] filters, Collection objects) {
		Object[] result = objects.toArray(new Object[objects.size()]);
		for (ViewerFilter f: filters) {
			Object[] tmp = f.filter(v, (Object) null, result);
			result = tmp;
		}
		return result;
	}
	
}
