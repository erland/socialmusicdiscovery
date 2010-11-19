package org.socialmusicdiscovery.rcp.views.navigator;


import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.socialmusicdiscovery.rcp.content.DataSource;
import org.socialmusicdiscovery.rcp.views.util.EntityLabelProvider;

public class NavigatorUI extends Composite {

	private TreeViewer treeViewer;

	public NavigatorUI(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		treeViewer = new TreeViewer(this, SWT.BORDER);
		treeViewer.setSorter(new ViewerSorter());
		treeViewer.setContentProvider(new NavigatorTreeContentProvider());
		treeViewer.setLabelProvider(new EntityLabelProvider());

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
