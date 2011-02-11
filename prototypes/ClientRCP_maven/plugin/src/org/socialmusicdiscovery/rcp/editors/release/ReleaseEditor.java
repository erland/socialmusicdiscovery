package org.socialmusicdiscovery.rcp.editors.release;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.socialmusicdiscovery.rcp.content.ObservableRelease;
import org.socialmusicdiscovery.rcp.editors.AbstractEditorPart;

public class ReleaseEditor extends AbstractEditorPart<ObservableRelease, ReleaseUI> {

	public static final String ID = ReleaseEditor.class.getName();

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent, new ReleaseUI(parent, SWT.NONE));
		hookContextMenus();
	}

	private void hookContextMenus() {
		hookContextMenus(
			getUI().getGridViewerTracks(),
			getUI().getPerformersPanel().getGridViewer(),
			getUI().getComposersPanel().getGridViewer(),
			getUI().getConductorsPanel().getGridViewer()
		);
	}

}
