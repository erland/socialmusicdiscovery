package org.socialmusicdiscovery.yggdrasil.foundation.views.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class NotYetImplementedUI extends Composite {

	public NotYetImplementedUI(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Label lblWorkInProgress = new Label(this, SWT.BORDER);
		lblWorkInProgress.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblWorkInProgress.setText("WORK IN PROGRESS - NOT YET IMPLEMENTED!!!");
	}
}
