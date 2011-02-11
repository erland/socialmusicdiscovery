package org.socialmusicdiscovery.rcp.editors.release;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
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
	private GridColumn gridColumn;

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

		gridColumn = new GridColumn(grid, SWT.NONE);
		gridColumn.setWidth(400);
		gridColumn.setText("Artist");
		
		hookListeners();
	}
	
	private void hookListeners() {
		// default edit
		gridTableViewer.addOpenListener(new OpenListener());
	}
	
	public GridTableViewer getGridViewer() {
		return gridTableViewer;
	}

	public GridColumn getGridColumn() {
		return gridColumn;
	}
}
