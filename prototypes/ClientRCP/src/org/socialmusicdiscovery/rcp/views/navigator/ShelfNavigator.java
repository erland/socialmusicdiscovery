package org.socialmusicdiscovery.rcp.views.navigator;


import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.socialmusicdiscovery.rcp.content.DataSource;

public class ShelfNavigator extends Composite {

	private PShelf shelf;
	private PShelfItem itemTree;
	private TreeNavigator treeNavigator;

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
		treeNavigator = new TreeNavigator(itemTree.getBody(), SWT.BORDER);
		
		createItems(5);
	}

	private void createItems(int size) {
		for (int i = 0; i < size; i++) {
			PShelfItem item = new PShelfItem(shelf, SWT.NONE);
			item.setText("Placeholder #"+i);
			item.getBody().setLayout(new FillLayout(SWT.HORIZONTAL));
			new PlaceHolder(item.getBody(), SWT.NONE);
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setInput(DataSource dataSource) {
		treeNavigator.setInput(dataSource);
	}

	public TreeNavigator getTreeNavigator() {
		return treeNavigator;
	}

	public void setView(ViewPart view) {
		treeNavigator.setView(view);
	}
}
