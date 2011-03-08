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

package org.socialmusicdiscovery.rcp.util;

import java.util.Comparator;

import org.eclipse.jface.util.Policy;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * Sorts specified column when column header is clicked. Default sorter uses label provider 
 * assigned by {@link ViewerUtil#bind(org.eclipse.jface.viewers.StructuredViewer, org.eclipse.core.databinding.observable.list.WritableList, org.eclipse.core.databinding.beans.IBeanValueProperty...)} 
 * or the default {@link Policy#getComparator()}.
 * 
 * TODO keep track of previous comparators to allow sorting on several columns.
 *   
 * @author Peer Törngren
 */
public class GridViewerColumnComparator extends ViewerComparator implements SelectionListener {

	private static class MyTableLabelProviderComparator implements Comparator {

		private final ITableLabelProvider provider;
		private final int index;

		private MyTableLabelProviderComparator(ITableLabelProvider labelProvider, int columnIndex) {
			this.provider = labelProvider;
			this.index = columnIndex;
		}

		@Override
		public int compare(Object o1, Object o2) {
			String s1 = provider.getColumnText(o1, index);
			String s2 = provider.getColumnText(o2, index);
			return s1.compareTo(s2);
		}

	}

	private static final String PRIMARY_SORT_COLUMN_KEY = "primarySortColumn"; //$NON-NLS-1$
	private static final int ASCENDING = 1;
	private static final int NO_SORT = 0;
	private static final int DESCENDING = -1;
	
	private final ColumnViewer viewer;
	private final GridColumn column;
	private final GridViewerColumn gvc;
	
	private Comparator comparator; // lazy init since we may register this instance before the label provider
	private int currentSortOrder = NO_SORT;
	
	/**
	 * Constructor for default sorter.
	 */
	public GridViewerColumnComparator(GridViewerColumn gvc) {
		this(gvc, null);
	}
	
	/**
	 * Constructor for specific sorter. 
	 */
	public GridViewerColumnComparator(GridViewerColumn gvc, Comparator comparator) {
		assert gvc!=null : "Must have viewer!"; //$NON-NLS-1$
		this.comparator = comparator;
		this.gvc = gvc;
		this.viewer = gvc.getViewer();
		this.column = gvc.getColumn();
		this.column.addSelectionListener(this);
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		changeSortOrder();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		changeSortOrder();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		return currentSortOrder * getComparator().compare(e1, e2);
	}

	@Override
	protected Comparator getComparator() {
		if (comparator==null) {
			comparator = resolveComparator(gvc);
		}
		return comparator;
	}
	
	private void changeSortOrder() {
		int sortOrder = viewer.getComparator() == this ? getNextSortOrder() : ASCENDING;
		setSortOrder(sortOrder);
	}

	private int getNextSortOrder() {
		switch (currentSortOrder) {
		case ASCENDING:
			return DESCENDING;
		case DESCENDING:
			return NO_SORT;
		case NO_SORT:
			return ASCENDING;
		default:
			throw new IllegalStateException("Unknown sort order state: "+currentSortOrder); //$NON-NLS-1$
		}
	}

	private void setSortOrder(int newSortOrder) {
		if (newSortOrder == NO_SORT ) {
			setPrimarySortColumn(null);
			column.setSort(SWT.NONE);
			viewer.setComparator(null);
		} else {
			setPrimarySortColumn(column);
			currentSortOrder = newSortOrder;
			int swtSortOrder = newSortOrder==ASCENDING ? SWT.UP: SWT.DOWN;
			column.setSort(swtSortOrder);
			if (viewer.getComparator() == this) {
				viewer.refresh();
			} else {
				viewer.setComparator(this);
			}
		}
	}

	private void setPrimarySortColumn(GridColumn columnOrNull) {
		Grid parent = column.getParent();
		GridColumn old = (GridColumn) parent.getData(PRIMARY_SORT_COLUMN_KEY);
		if (old!=null) {
			old.setSort(NO_SORT);
		}
		parent.setData(PRIMARY_SORT_COLUMN_KEY, columnOrNull);
	}
	
	private Comparator resolveComparator(GridViewerColumn gvc) {
		IBaseLabelProvider lp = gvc.getViewer().getLabelProvider();
		if (lp instanceof ITableLabelProvider) {
			int columnIndex = ViewerUtil.resolveColumnIndex(gvc);
			return new MyTableLabelProviderComparator((ITableLabelProvider) lp, columnIndex);
		}
		return Policy.getComparator(); // emergency: default comparator will probably not work as expected 
	}

}