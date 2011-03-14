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

/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 *                                               - fix for bug 178280
 *     IBM Corporation - API refactoring and general maintenance
 *******************************************************************************/

package org.socialmusicdiscovery.rcp.grid;

import org.eclipse.jface.layout.AbstractColumnLayout;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Widget;

/**
 * <p>A port of the {@link TableColumnLayout} for the Nebula {@link Grid}.
 * Extremely simple, only changed references to {@link Table} and
 * {@link TableColumn} to {@link Grid} and {@link GridColumn}.</p>
 * 
 * <p>
 * <b>You can only add the {@link Layout} to a container whose <i>only</i> child
 * is the {@link Table} control you want the {@link Layout} applied to. Don't
 * assign the layout directly the {@link Table}</b>
 * </p>
 * @see TableColumnLayout
 */
public class GridTableColumnLayout extends AbstractColumnLayout {

	private static final boolean IS_GTK = Util.isGtk();


	protected int getColumnCount(Scrollable grid) {
		return ((Grid) grid).getColumnCount();
	}
	protected void setColumnWidths(Scrollable grid, int[] widths) {
		GridColumn[] columns = ((Grid) grid).getColumns();
		for (int i = 0; i < widths.length; i++) {
			columns[i].setWidth(widths[i]);
		}
	}
	protected ColumnLayoutData getLayoutData(Scrollable grid, int columnIndex) {
		GridColumn column = ((Grid) grid).getColumn(columnIndex);
		return (ColumnLayoutData) column.getData(LAYOUT_DATA);
	}

	Composite getComposite(Widget column) {
		return ((GridColumn) column).getParent().getParent();
	}

	protected void updateColumnData(Widget column) {
		GridColumn gColumn = (GridColumn) column;
		Grid g = gColumn.getParent();

		if (!IS_GTK || g.getColumn(g.getColumnCount() - 1) != gColumn) {
			gColumn.setData(LAYOUT_DATA, new ColumnPixelData(gColumn.getWidth()));
			layout(g.getParent(), true);
		}
	}
}
