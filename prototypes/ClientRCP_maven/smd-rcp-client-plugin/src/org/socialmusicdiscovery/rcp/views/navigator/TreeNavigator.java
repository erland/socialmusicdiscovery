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

package org.socialmusicdiscovery.rcp.views.navigator;


import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.socialmusicdiscovery.rcp.content.DataSource;
import org.socialmusicdiscovery.rcp.util.ViewerUtil;
import org.socialmusicdiscovery.rcp.views.util.LabelProviderFactory;
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
		
		bindViewer();
	}

	/**
	 * Do we want this, or should we call
	 * {@link ViewerSupport#bind(org.eclipse.jface.viewers.AbstractTreeViewer, Object, org.eclipse.core.databinding.property.list.IListProperty, org.eclipse.core.databinding.property.value.IValueProperty)}
	 * when we set input? Not sure which is simpler or better.
	 */
	private void bindViewer() {
		treeViewer.setSorter(new ViewerSorter());
		ObservableListTreeContentProvider contentProvider = new ObservableListTreeContentProvider(new NavigatorListFactory(), new NavigatorStructureAdvisor());
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(LabelProviderFactory.defaultObservable(contentProvider));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setInput(DataSource dataSource) {
		getTreeViewer().setInput(dataSource);
//		ViewerSupport.bind(getTreeViewer(), dataSource.getRoots(), BeanProperties.list("observableChildren"), BeanProperties.value("name"));
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	public void setView(ViewPart view) {
		ViewerUtil.hookContextMenu(view, treeViewer );
		treeViewer.addOpenListener(new OpenListener());
	}
}
