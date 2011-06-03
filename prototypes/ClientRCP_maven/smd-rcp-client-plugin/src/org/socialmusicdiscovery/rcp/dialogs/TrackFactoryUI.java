/*
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

package org.socialmusicdiscovery.rcp.dialogs;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.socialmusicdiscovery.rcp.content.ObservableRecording;
import org.socialmusicdiscovery.rcp.content.ObservableRelease;
import org.socialmusicdiscovery.rcp.content.ObservableTrack;
import org.socialmusicdiscovery.rcp.content.RecordingProvider;
import org.socialmusicdiscovery.rcp.editors.widgets.SelectionPanel;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.socialmusicdiscovery.rcp.views.util.DefaultLabelProvider;
import org.socialmusicdiscovery.server.business.model.core.Medium;

/**
 * Creates an {@link ObservableTrack} instance. Place on a container.
 * 
 * @author Peer Törngren
 *
 */
public class TrackFactoryUI extends Composite {

	private Composite composite;
	private Combo mediumCombo;
	private ComboViewer mediumViewer;
	private Label mediumLabel;
	private Label ownerLabel;
	private Text releaseText;
	private Label infoLabel;
	
	// simplify data binding in UI tool + expose observable properties to parent
	private final ObservableTrack template = new ObservableTrack(); 
	private SelectionPanel<ObservableRecording> selectionPanel;
	private Composite indexArea;
	private Label trackLabel;
	private Spinner trackSpinner;

	/**
	 * Create the dialog.
	 * @param parent
	 */
	public TrackFactoryUI(Composite parent, int style) {
		super(parent, style);
		setBackgroundMode(SWT.INHERIT_DEFAULT);

		setLayout(new FillLayout(SWT.HORIZONTAL));
		composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		infoLabel = new Label(composite, SWT.WRAP);
		infoLabel.setText("Create a new track by assigning medium and track numbers to a new recording.");
		infoLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		ownerLabel = new Label(composite, SWT.NONE);
		ownerLabel.setText("Release:");
		ownerLabel.setToolTipText("What release is this track on?");
		
		releaseText = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		releaseText.setText("");
		releaseText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		indexArea = new Composite(composite, SWT.NONE);
		indexArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		GridLayout gl_indexArea = new GridLayout(2, false);
		gl_indexArea.marginHeight = 0;
		gl_indexArea.marginWidth = 0;
		indexArea.setLayout(gl_indexArea);
		
		mediumLabel = new Label(indexArea, SWT.NONE);
		mediumLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		mediumLabel.setText("Medium:");
		mediumLabel.setToolTipText("What medium (if any) is this track on? Leave blank if this release has no media.");
		
		trackLabel = new Label(indexArea, SWT.NONE);
		trackLabel.setToolTipText("What is the number of his track? Set to 0 or blank if number is not known.");
		trackLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		trackLabel.setText("Track:");
		
		mediumViewer = new ComboViewer(indexArea, SWT.READ_ONLY);
		mediumViewer.setSorter(new ViewerSorter());
		mediumCombo = mediumViewer.getCombo();
		mediumCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		mediumViewer.setLabelProvider(new DefaultLabelProvider());
		
		trackSpinner = new Spinner(indexArea, SWT.BORDER);
		trackSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		mediumViewer.setContentProvider(new ArrayContentProvider());
		
		selectionPanel = new SelectionPanel<ObservableRecording>(composite, SWT.NONE);
		selectionPanel.getLabel().setToolTipText("What is this track?");
		selectionPanel.getButton().setToolTipText("Select the recording that this track represents on this release.");
		GridLayout gridLayout = (GridLayout) selectionPanel.getLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		selectionPanel.getText().setEditable(true);
		selectionPanel.getLabel().setText("Recording:");
		selectionPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		initDataBindings();
	}

	void setRelease(ObservableRelease release) {
		template.setRelease(release);
		releaseText.setText(release.getName());
	}

	public void setNumber(Integer number) {
		template.setNumber(number);
	}

	public void setMedium(Medium medium) {
		template.setMedium(medium);
	}

	public void setRecording(ObservableRecording recording) {
		template.setRecording(recording);
	}
	
	void setRecordingProvider(RecordingProvider provider) {
		selectionPanel.setElementProvider(provider);
	}


	public ObservableTrack getTemplate() {
		return template;
	}
	public ComboViewer getMediumViewer() {
		return mediumViewer;
	}
	public Spinner getTrackSpinner() {
		return trackSpinner;
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue mediumViewerObserveSingleSelection = ViewersObservables.observeSingleSelection(mediumViewer);
		IObservableValue templateMediumObserveValue = BeansObservables.observeValue(template, "medium");
		bindingContext.bindValue(mediumViewerObserveSingleSelection, templateMediumObserveValue, null, null);
		//
		IObservableValue trackSpinnerObserveSelectionObserveWidget = SWTObservables.observeSelection(trackSpinner);
		IObservableValue templateNumberObserveValue = BeansObservables.observeValue(template, "number");
		bindingContext.bindValue(trackSpinnerObserveSelectionObserveWidget, templateNumberObserveValue, null, null);
		//
		selectionPanel.bindSelection(bindingContext, template, ObservableTrack.PROP_recording);
		return bindingContext;
	}

}
