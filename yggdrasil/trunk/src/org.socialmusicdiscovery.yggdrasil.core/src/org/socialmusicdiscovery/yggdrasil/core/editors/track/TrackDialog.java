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

package org.socialmusicdiscovery.yggdrasil.core.editors.track;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.socialmusicdiscovery.server.business.model.core.Medium;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableRecording;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableRelease;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableTrack;
import org.socialmusicdiscovery.yggdrasil.foundation.content.RecordingProvider;
import org.socialmusicdiscovery.yggdrasil.foundation.dialogs.FactoryDialog;
import org.socialmusicdiscovery.yggdrasil.foundation.editors.AbstractEditorDialog;
import org.socialmusicdiscovery.yggdrasil.foundation.util.ClassUtil;
import org.socialmusicdiscovery.yggdrasil.foundation.util.Util;

/**
 * A dialog for editing or creating a new {@link Track}.
 * 
 * @author Peer Törngren
 *
 */
public class TrackDialog extends AbstractEditorDialog<ObservableTrack> implements FactoryDialog<ObservableRelease, ObservableTrack> {

	/**
	 * Preliminary stub. Need to think about how to handle validation properly.
	 */
	private class MyButtonManager implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			okButton.setEnabled(isValid(ui.getTemplate()));
		}
		
		private boolean isValid(Track prospect) {
			return isFullyInitialized(prospect) && (isUnique(prospect) || isChangedRecording(prospect));
		}

		private boolean isChangedRecording(Track prospect) {
			return prospect.getRecording()!=recording && (prospect.getMedium()==medium && prospect.getNumber()==number);
		}

		private boolean isFullyInitialized(Track template) {
			return template.getRecording()!=null;
		}
		
		private boolean isUnique(Track prospect) {
			List<Track> tracks = release.getTracks();
			for (Track t : tracks) {
				if (equalIndex(t, prospect)) {
					return false;
				}
			}
			return true;
		}

		private boolean equalIndex(Track t1, Track t2) {
			int trackNumberDiff = Util.compare(t1.getNumber(), t2.getNumber());
			return trackNumberDiff==0 && t1.getMedium()==t2.getMedium(); 
		}

	}
	
	private ObservableRelease release;
	private TrackUI ui;
	private Medium medium;
	private Integer number;
	private ObservableRecording recording;
	private ObservableTrack originalInput;
	// enabled only on new tracks; to change recording, track must be deleted and a new track added
//	private boolean recordingSelectorEnabled; 

	/**
	 * Must have constructor without args to allow
	 * instantiation from extension registry.
	 */
	public TrackDialog() {
		super(new Shell(), "Edit Track");
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
//		ui.getSelectionPanel().setEnabled(recordingSelectorEnabled);

		return area;
	}

	@Override
	public void create() {
		super.create();
		ui.getTemplate().addPropertyChangeListener(new MyButtonManager());
	}

	@Override
	protected void beforeEdit(ObservableTrack input) {
		originalInput = input;
		release = input.getRelease();
		medium = input.getMedium();
		number = input.getNumber();
		recording = input.getRecording();
//		recordingSelectorEnabled = false;
	}
	
	@Override
	protected ObservableTrack afterEdit() {
		ClassUtil.copyProperties(ui.getTemplate(), originalInput, ObservableTrack.PROP_release, ObservableTrack.PROP_medium, ObservableTrack.PROP_number, ObservableTrack.PROP_recording);
		return originalInput;
	}

	@Override
	public ObservableTrack createChild(ObservableRelease parent) {
		release = parent;
//		dlg.recordingSelectorEnabled = true;
		if (openOK()) {
			return new ObservableTrack(ui.getTemplate());
		}
		return null;
	}

}