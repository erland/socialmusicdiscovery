package org.socialmusicdiscovery.rcp.editors.artist;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.socialmusicdiscovery.rcp.editors.AbstractEditorPart;
import org.socialmusicdiscovery.server.business.model.core.Artist;

public class ArtistEditor extends AbstractEditorPart<Artist, ArtistUI> {

	public static final String ID = ArtistEditor.class.getName();

	public ArtistEditor() {
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent, new ArtistUI(parent, SWT.NONE));
	}

}
