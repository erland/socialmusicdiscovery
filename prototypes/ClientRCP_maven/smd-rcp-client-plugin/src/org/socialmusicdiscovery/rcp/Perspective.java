package org.socialmusicdiscovery.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.socialmusicdiscovery.rcp.views.navigator.NavigatorView;

public class Perspective implements IPerspectiveFactory {

//	private static final String PLACEHOLDER_PATTERN = "org.socialmusicdiscovery.rcp.views.*";
	public final static String ID = Perspective.class.getName();
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		
		layout.addStandaloneView(NavigatorView.ID,  false, IPageLayout.LEFT, 0.25f, editorArea);
//		IFolderLayout folder = layout.createFolder("views", IPageLayout.BOTTOM, 0.80f, editorArea);
//		folder.addPlaceholder(PLACEHOLDER_PATTERN);
		
		layout.getViewLayout(NavigatorView.ID).setCloseable(false);
	}
}
