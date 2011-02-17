package org.socialmusicdiscovery.rcp.views.navigator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

class PlaceHolder extends Composite {

	private Label info;

	public PlaceHolder(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		info = new Label(this, SWT.NONE);
		info.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		info.setBounds(0, 0, 49, 13);
		info.setText("(empty space)");
	}

	public PlaceHolder(Composite parent, int style, String message) {
		this(parent, style);
		info.setText(message);
	}

}
