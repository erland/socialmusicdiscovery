package org.socialmusicdiscovery.rcp.editors.artist;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.socialmusicdiscovery.rcp.content.ObservableArtist;
import org.socialmusicdiscovery.rcp.editors.AbstractEditorPart;

public class ArtistEditor extends AbstractEditorPart<ObservableArtist, ArtistUI> {

	public static final String ID = ArtistEditor.class.getName();

	public ArtistEditor() {
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent, new ArtistUI(parent, SWT.NONE));
	}

}
