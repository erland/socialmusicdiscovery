package org.socialmusicdiscovery.rcp.views.navigator;


import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.socialmusicdiscovery.rcp.content.DataSource;
import org.socialmusicdiscovery.rcp.util.ViewerUtil;
import org.socialmusicdiscovery.rcp.views.util.DefaultLabelProvider;
import org.socialmusicdiscovery.rcp.views.util.OpenListener;

/**
 * A classic tree-based browser/navigator for a few traditional (and presumably
 * commonly desired) hierarchies.
 * 
 * @author Peer Törngren
 * 
 */
public class TreeNavigator extends Composite {

	private TreeViewer treeViewer;

	public TreeNavigator(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		treeViewer = new TreeViewer(this, SWT.NONE);
		
		treeViewer.setSorter(new ViewerSorter());
		ObservableListTreeContentProvider contentProvider = new ObservableListTreeContentProvider(new NavigatorListFactory(), new NavigatorStructureAdvisor());
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(new DefaultLabelProvider());
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

	public void setView(ViewPart view) {
		ViewerUtil.hookContextMenu(view, treeViewer );
		treeViewer.addOpenListener(new OpenListener());
	}
}
