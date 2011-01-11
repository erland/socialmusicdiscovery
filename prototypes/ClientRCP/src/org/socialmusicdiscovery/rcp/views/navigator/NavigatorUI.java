package org.socialmusicdiscovery.rcp.views.navigator;


import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.socialmusicdiscovery.rcp.content.DataSource;
import org.socialmusicdiscovery.rcp.views.util.DefaultLabelProvider;

public class NavigatorUI extends Composite {

	private TreeViewer treeViewer;
	private Composite otherArea;
	private Label lblplaceholder;
	private PShelf shelf;
	private PShelfItem itemTree;
	private PShelfItem itemOther;

	public NavigatorUI(Composite parent, int style) {
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
		
		treeViewer = new TreeViewer(itemTree.getBody(), SWT.BORDER);
		
		itemOther = new PShelfItem(shelf, SWT.NONE);
		itemOther.setText("Other");
		treeViewer.setSorter(new ViewerSorter());
		ObservableListTreeContentProvider contentProvider = new ObservableListTreeContentProvider(new NavigatorListFactory(), new NavigatorStructureAdvisor());
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(new DefaultLabelProvider());
		itemOther.getBody().setLayout(new FillLayout(SWT.HORIZONTAL));
		
		otherArea = new Composite(itemOther.getBody(), SWT.NONE);
		otherArea.setLayout(new GridLayout(1, false));
		
		lblplaceholder = new Label(otherArea, SWT.NONE);
		lblplaceholder.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		lblplaceholder.setBounds(0, 0, 49, 13);
		lblplaceholder.setText("(placeholder)");
		Label todoLabel = new Label(otherArea, SWT.NONE);
		todoLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		todoLabel.setText("TODO: fix layout");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setInput(DataSource dataSource) {
		getTreeViewer().setInput(dataSource);
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}
}
