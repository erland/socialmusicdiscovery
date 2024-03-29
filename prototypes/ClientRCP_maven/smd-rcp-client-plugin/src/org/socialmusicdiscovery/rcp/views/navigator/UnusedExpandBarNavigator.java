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


import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.socialmusicdiscovery.rcp.content.DataSource;

/**
 * <p>This class is NOT maintained! It is saved as a reference and possible
 * replacement for the PShelf version.</p>
 * 
 * <p>Rationale - Apart from the visual appearance, the main differences are:
 * <ul>
 * <li>ExpandBar allows several items to be open at once, PShelf allows only one
 * at a time.</li>
 * <li>ExpandBar is supported by RAP and can be used in a Web client.</li>
 * <li>The job of switching between PShelf and ExpandBar was quick ( a few
 * minutes)</li>
 * <li>I don't know which one will be easiest to make extensible</li>
 * <li>ExpandBar does not grab excess space - must create my own layout manager.
 * This is not rocket science, but ut will take time.</li>
 * </ul>
 * See these links:
 * <ul>
 * <li>
 * http://dev.eclipse.org/viewcvs/viewvc.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet343.java?view=co</li>
 * <li>
 * http://stackoverflow.com/questions/586414
 * /why-does-an-swt-composite-sometimes-
 * require-a-call-to-resize-to-layout-correctl</li>
 * <li>
 * http://www.eclipsezone.com/eclipse/forums/t76814.html</li>
 * </ul>
 * All things considered, especially the need for a layout manager, I will go
 * with PShelf for the time being. If/when I (or someone else) have the time to
 * write a layout manager that handles all items in the desired way (similar to
 * a TableColumnLayout), and/or we want to run the client as a RAP application,
 * we can switch back. Meanwhile. this version is saved as a reference, but I
 * will only maintain the PShelf version.</p>
 * 
 * 
 * @author Peer Törngren
 * 
 */
public class UnusedExpandBarNavigator extends Composite {

	private final class MyLayoutManager implements Listener {
	// TODO recalculate heights properly. Need something like a TableColumnLayout?
    // see http://dev.eclipse.org/viewcvs/viewvc.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet343.java?view=co
    // see http://stackoverflow.com/questions/586414/why-does-an-swt-composite-sometimes-require-a-call-to-resize-to-layout-correctl
    // see http://www.eclipsezone.com/eclipse/forums/t76814.html
//		you can use ExpandItem#setHeight to control how much space the item takes
//		when it is expand. You can actually change this value on the fly when
//		other item expands or the widget resizes.
//		you can use ExpandItem#getHeaderHeight() to get the height of the title.
//
//		This snippet forces the item[0] to take up all the available space in the
//		parent:
		public void handleEvent(Event e) {
			getDisplay().asyncExec(new Runnable() {
				public void run() {
					ExpandItem[] items = expandBar.getItems();
					Rectangle area = expandBar.getClientArea();
					int spacing = expandBar.getSpacing();
					// area.width -= 2*spacing;
					int header0 = items[0].getHeaderHeight();
					int header1 = items[1].getHeaderHeight();
					area.height -= (items.length + 1) * spacing + header0 + header1;
					if (items[1].getExpanded()) {
						area.height -= items[1].getHeight();// + spacing;
					}
					items[0].setHeight(area.height);
				}
			});
		}
	}

	private TreeNavigator treeComposite;
	private ExpandBar expandBar;
	private ExpandItem itemTree;
	private ExpandItem itemOther;
	private Composite otherArea;
	private Label lblplaceholder;

	public UnusedExpandBarNavigator(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		expandBar = new ExpandBar(this, SWT.NONE);
		expandBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		itemTree = new ExpandItem(expandBar, SWT.NONE);
		itemTree.setExpanded(true);
		itemTree.setText("Basic Tree");
		
		treeComposite = new TreeNavigator(expandBar, SWT.BORDER);
		itemTree.setControl(treeComposite);
		itemTree.setHeight(-1);
		
		itemOther = new ExpandItem(expandBar, SWT.NONE);
		itemOther.setText("Other View");
		
		otherArea = new Composite(expandBar, SWT.NONE);
		itemOther.setControl(otherArea);
		otherArea.setLayout(new GridLayout(1, false));
		
		lblplaceholder = new Label(otherArea, SWT.NONE);
		lblplaceholder.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		lblplaceholder.setBounds(0, 0, 49, 13);
		lblplaceholder.setText("(placeholder)");
		Label todoLabel = new Label(otherArea, SWT.NONE);
		todoLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		todoLabel.setText("TODO: fix layout");
		itemOther.setHeight(itemOther.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
	    hookLayoutManager();
	}

	private void hookLayoutManager() {
		Listener listener = new MyLayoutManager();
		expandBar.addListener(SWT.Resize, listener);
		expandBar.addListener(SWT.Expand, listener);
		expandBar.addListener(SWT.Collapse, listener);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setInput(DataSource dataSource) {
		getTreeViewer().setInput(dataSource);
	}

	public TreeViewer getTreeViewer() {
		return treeComposite.getTreeViewer();
	}
}
