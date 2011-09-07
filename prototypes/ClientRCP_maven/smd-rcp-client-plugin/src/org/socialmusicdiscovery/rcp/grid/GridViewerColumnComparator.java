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

package org.socialmusicdiscovery.rcp.grid;

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
import org.socialmusicdiscovery.rcp.util.ViewerUtil;

/**
 * <p>
 * Sorts specified column when column header is clicked. Default sorter uses
 * label provider assigned by
 * {@link ViewerUtil#bind(org.eclipse.jface.viewers.StructuredViewer, org.eclipse.core.databinding.observable.list.WritableList, org.eclipse.core.databinding.beans.IBeanValueProperty...)}
 * or the default {@link Policy#getComparator()}.
 * </p>
 * 
 * <p>
 * Keeps track of previous comparators to allow sorting on several columns; if
 * first sorting on column A and then on column B, the sort order of A will
 * apply if sorting on B renders 0 (no difference). Only the primary sort column
 * is indicated in UI. If sorting is turned off, all history is gone.
 * </p>
 * 
 * @author Peer Törngren
 */
@SuppressWarnings("rawtypes")
public class GridViewerColumnComparator extends ViewerComparator implements SelectionListener {

	private static class VoidComparator extends ViewerComparator {
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			return 0;
		}
	}

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
	private final Comparator comparatorOrNull;
	
	private int currentSortOrder = NO_SORT;
	private ViewerComparator subSorter = new VoidComparator();
	
	/**
	 * Creates and registers a sorter with default comparator (dynamically resolved from label provider).
	 */
	public static GridViewerColumnComparator hook(GridViewerColumn gvc) {
		return new GridViewerColumnComparator(gvc, null);
	}
	
	/**
	 * Creates and registers a sorter with supplied comparator.  
	 */
	public static GridViewerColumnComparator hook(GridViewerColumn gvc, Comparator comparator) {
		return new GridViewerColumnComparator(gvc, null);
	}

	/**
	 * Private constructor. Use static factory methods.
	 */
	private GridViewerColumnComparator(GridViewerColumn gvc, Comparator comparator) {
		assert gvc!=null : "Must have viewer!"; //$NON-NLS-1$
		this.comparatorOrNull = comparator;
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
		assert subSorter!=this : "Self is subsorter: "+this;
		int primarySort = currentSortOrder * getComparator().compare(e1, e2);
		int finalSort = primarySort==0 ? subSorter.compare(viewer, e1, e2) : primarySort;
		return finalSort ;
	}

	/**
	 * Implementation note: do <b>not</b> cache the comparator since it dependes on 
	 * the concrete label provider instance that is rebound when the model changes.
	 * Unless we got a stable comparator passed in the constructor, we need to resolve it
	 * on every call (or find a safe way of caching and disposing with the label provider).
	 * Until we see a performance problem here, it isn't worth the trouble. 
	 */
	@Override
	protected Comparator getComparator() {
		return comparatorOrNull==null ? resolveComparator(gvc) : comparatorOrNull;
	}
	
	/**
	 * Create a comparator based on the label provider of the column. See
	 * comments on {@link #getComparator()}.
	 * 
	 * @param gvc
	 * @return {@link Comparator}
	 */
	private static Comparator resolveComparator(GridViewerColumn gvc) {
		IBaseLabelProvider lp = gvc.getViewer().getLabelProvider();
		if (lp instanceof ITableLabelProvider) {
			int columnIndex = ViewerUtil.resolveColumnIndex(gvc);
			return new MyTableLabelProviderComparator((ITableLabelProvider) lp, columnIndex);
		}
		return Policy.getComparator(); // emergency: default comparator will probably not work as expected 
	}

	private void changeSortOrder() {
		boolean isInitialSort = viewer.getComparator() != this;
		int sortOrder = isInitialSort ? ASCENDING : getNextSortOrder(currentSortOrder);
		
		currentSortOrder = sortOrder;
		if (sortOrder == NO_SORT ) {
			setNoSortOrder();
		} else {
			setSortOrder(sortOrder);
		}
	}

	private void setNoSortOrder() {
		setPrimarySortColumn(null, SWT.NONE);
		viewer.setComparator(null);
		clearSubSorter();
	}

	private void setSortOrder(int newSortOrder) {
		int swtSortOrder = newSortOrder==ASCENDING ? SWT.UP: SWT.DOWN;
		setPrimarySortColumn(column, swtSortOrder);

		ViewerComparator currentSorter = viewer.getComparator();
		if (currentSorter == this) {
			viewer.refresh();
		} else {
			if (currentSorter!=null) {
				subSorter = currentSorter;
				breakSubSorterLoop();
			}
			viewer.setComparator(this); // AFTER setting subsorter - sorting is done when comparator is set 
		}
	}

	private void setPrimarySortColumn(GridColumn primarySortColumnOrNull, int swtSortOrder) {
		Grid parent = column.getParent();
		GridColumn oldSortColumn = (GridColumn) parent.getData(PRIMARY_SORT_COLUMN_KEY);
		parent.setData(PRIMARY_SORT_COLUMN_KEY, primarySortColumnOrNull);
		
		if (oldSortColumn!=null) {
			oldSortColumn.setSort(NO_SORT);
		}
		
		// after reset - if it's the same column ...
		if (primarySortColumnOrNull!=null) {
			primarySortColumnOrNull.setSort(swtSortOrder);
		}
		
	}

	/**
	 * Avoid infinite loops if user sorts on same columns several times -
	 * recursively traverse chain of sub-sorters and break on first occurrence of
	 * this instance. Elegant? No. Works? Yes.
	 * 
	 */
	private void breakSubSorterLoop() {
		GridViewerColumnComparator loopPoint = findSubsorter(this);
		if (loopPoint!=null) {
			loopPoint.clearSubSorter();
		}
	}

	/**
	 * Recursively traverse chain of subsorters and break on first occurrence of
	 * this instance (first sorter that uses this instance as subsorter).
	 * 
	 * @param sorter or <code>null</code>
	 */
	private GridViewerColumnComparator findSubsorter(Object sorter) {
		if (sorter instanceof GridViewerColumnComparator) {
			GridViewerColumnComparator gvcc = (GridViewerColumnComparator) sorter;
			ViewerComparator suspectSubSorter = gvcc.subSorter;
			return suspectSubSorter==this ? gvcc : findSubsorter(suspectSubSorter); 
		}
		return null;
	}
	
	/**
	 * Reset the subsorter. 
	 */
	private void clearSubSorter() {
		subSorter = new VoidComparator();
	}

	private static int getNextSortOrder(int currentSortOrder) {
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


}