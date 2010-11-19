package org.socialmusicdiscovery.rcp.editors.release;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.socialmusicdiscovery.rcp.editors.AbstractEditorPart;
import org.socialmusicdiscovery.server.business.model.core.Release;

public class ReleaseEditor extends AbstractEditorPart<Release> {

	public static final String ID = ReleaseEditor.class.getName();

	public ReleaseEditor() {
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent, new ReleaseUI(parent, SWT.NONE));
	}

}
