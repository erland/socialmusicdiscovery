package org.socialmusicdiscovery.rcp.views.navigator;


import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.socialmusicdiscovery.rcp.content.DataSource;

public class ShelfNavigator extends Composite {

	private PShelf shelf;
	private PShelfItem itemTree;
	private PShelfItem other1;
	private TreeNavigator tree;
	private PShelfItem other2;

	public ShelfNavigator(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		shelf = new PShelf(this, SWT.NONE);
		shelf.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		itemTree = new PShelfItem(shelf, SWT.NONE);
		itemTree.setText("Tree");
		itemTree.getBody().setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tree = new TreeNavigator(itemTree.getBody(), SWT.BORDER);
		
		other1 = new PShelfItem(shelf, SWT.NONE);
		other1.setText("Placeholder 1");
		other1.getBody().setLayout(new FillLayout(SWT.HORIZONTAL));
		new PlaceHolder(other1.getBody(), SWT.NONE);

		other2 = new PShelfItem(shelf, SWT.NONE);
		other2.setText("Placeholder 2");
		other2.getBody().setLayout(new FillLayout(SWT.HORIZONTAL));
		new PlaceHolder(other2.getBody(), SWT.NONE);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setInput(DataSource dataSource) {
		getTreeViewer().setInput(dataSource);
	}

	public TreeViewer getTreeViewer() {
		return tree.getTreeViewer();
	}
}
