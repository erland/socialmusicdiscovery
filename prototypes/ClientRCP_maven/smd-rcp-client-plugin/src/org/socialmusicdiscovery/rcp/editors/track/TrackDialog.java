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

package org.socialmusicdiscovery.rcp.editors.track;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.socialmusicdiscovery.rcp.content.ObservableEntity;
import org.socialmusicdiscovery.rcp.content.ObservableRecording;
import org.socialmusicdiscovery.rcp.content.ObservableRelease;
import org.socialmusicdiscovery.rcp.content.ObservableTrack;
import org.socialmusicdiscovery.rcp.content.RecordingProvider;
import org.socialmusicdiscovery.rcp.editors.AbstractEditorDialog;
import org.socialmusicdiscovery.rcp.util.Util;
import org.socialmusicdiscovery.server.business.model.core.Medium;
import org.socialmusicdiscovery.server.business.model.core.Track;

/**
 * A dialog for creating a new {@link Track}.
 * 
 * @author Peer Törngren
 *
 */
public class TrackDialog extends AbstractEditorDialog<ObservableTrack> {

	/**
	 * Preliminary stub. Need to think about how to handle validation properly.
	 */
	private class MyButtonManager implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			okButton.setEnabled(isValid(ui.getTemplate()));
		}
		
		private boolean isValid(ObservableTrack prospect) {
			return isFullyInitialized(prospect) && isUnique(prospect);
		}

		private boolean isFullyInitialized(Track template) {
			return template.getRecording()!=null;
		}
		
		@SuppressWarnings("unchecked")
		private boolean isUnique(Track prospect) {
			List<Track> tracks = release.getTracks();
			for (Track t : tracks) {
				if (equal(t, prospect)) {
					return false;
				}
			}
			return true;
		}

		private boolean equal(Track t1, Track t2) {
			int trackNumberDiff = Util.compare(t1.getNumber(), t2.getNumber());
			return trackNumberDiff==0 && t1.getMedium()==t2.getMedium(); 
		}

	}
	
	private ObservableRelease release;
	private TrackUI ui;
	private Medium medium;
	private Integer number;
	private ObservableRecording recording;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public TrackDialog(Shell parentShell) {
		super(parentShell, "Edit Track");
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
//		setMessage("Create a new contributor by selecting type of contribution and an artist. The combination of type and artist must be unique.");
		Composite area = (Composite) super.createDialogArea(parent);
		area.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		ui = new TrackUI(area, SWT.NONE);
		ui.setRecordingProvider(new RecordingProvider());
		ui.getMediumViewer().setInput(release.getMediums());
		ui.setRelease(release);
		ui.setNumber(number);
		ui.setMedium(medium);
		ui.setRecording(recording);

		return area;
	}

	@Override
	public void create() {
		super.create();
		ui.getTemplate().addPropertyChangeListener(new MyButtonManager());
	}

	public static ObservableTrack open(ObservableRelease release) {
		TrackDialog dlg = new TrackDialog(null);
		dlg.release = release;
		if (openOK(dlg)) {
			return new ObservableTrack(dlg.ui.getTemplate());
		}
		return null;
	}

	public static ObservableEntity open(ObservableTrack input) {
		TrackDialog dlg = new TrackDialog(null);
		dlg.release = input.getRelease();
		dlg.medium = input.getMedium();
		dlg.number = input.getNumber();
		dlg.recording = input.getRecording();
		if (openOK(dlg)) {
			Util.mergeInto(input, dlg.ui.getTemplate());
		}
		return input;
	}

}