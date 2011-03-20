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

package org.socialmusicdiscovery.rcp.content;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Medium;
import org.socialmusicdiscovery.server.business.model.core.PlayableElement;
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.socialmusicdiscovery.server.business.model.core.RecordingSession;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;

import com.google.gson.annotations.Expose;

public class ObservableTrack extends AbstractObservableEntity<Track> implements Track {

	/**
	 * Lots of unsafe types here - see comments on {@link AbstractContributableEntity}.
	 * @param contributables
	 */
	@SuppressWarnings("unchecked")
	private class MyContributorFacade extends AbstractContributableEntity {
		
		private MyContributorFacade() {
			compileContributors(resolveContributables());
		}

		public Set resolveContributables() {
			Set result = new HashSet(getRecording().getWorks());
			result.addAll(Arrays.asList(
					resolveRecordingSession(getRecording()),
					getRecording(), 
					getRelease()
					));
			result.retainAll(Arrays.asList(getRecording())); // FIXME remove when filters work in UI 
			return result;
		}

		/**
		 * FIXME stub - should move to some utility method or something?
		 * @param recording
		 * @return <code>null</code>
		 */
		public RecordingSession resolveRecordingSession(Recording recording) {
			return null; // getRecordingSession();
		}
	
		private void compileContributors(Collection contributables) {
			Set<Contributor> contributors = getContributors();
			contributors.clear();
			for (Object o: contributables) {
				if (o!=null) {
					AbstractContributableEntity e = (AbstractContributableEntity) o;
					contributors.addAll(e.getContributors());
				}
			}
			firePropertyChange(PROP_contributors);
		}
	}

	public static final String PROP_number = "number";
	public static final String PROP_playableElements = "playableElements";
	public static final String PROP_medium = "medium";
	public static final String PROP_recording = "recording";
	public static final String PROP_release = "release";
	public static final String PROP_title = "title";
	
	private transient String title;
	
	@Expose private Integer number;
	@Expose private Medium medium;
	@Expose private Recording recording;
	@Expose private Set<PlayableElement> playableElements = new HashSet<PlayableElement>();
	@Expose private Release release;
	private MyContributorFacade contributorFacade;

	@Override
	public Integer getNumber() {
		return number;
	}

	@Override
	public Medium getMedium() {
		return medium;
	}

	@Override
	public Recording getRecording() {
		return recording;
	}

	@Override
	public Set<PlayableElement> getPlayableElements() {
		return playableElements;
	}

	@Override
	public Release getRelease() {
		return release;
	}

	public void setNumber(Integer number) {
		firePropertyChange(PROP_number, this.number, this.number = number);
	}

	public void setPlayableElements(Set<PlayableElement> playableElements) {
		firePropertyChange(PROP_playableElements, this.playableElements, this.playableElements = playableElements);
	}

	public void setMedium(Medium medium) {
		firePropertyChange(PROP_medium, this.medium, this.medium = medium);
	}

	public void setRecording(Recording recording) {
		firePropertyChange(PROP_recording, this.recording, this.recording = recording);
	}

	public void setRelease(Release release) {
		firePropertyChange(PROP_release, this.release, this.release = release);
	}

	public String getTitle() {
		// FIXME observe recording
		if (title==null) {
			hookTitle();
		}
		return title;
	}

	private void hookTitle() {
		ObservableRecording r = getObservableRecording();
		title = r.getName();
	}

	public void setTitle(String title) {
		firePropertyChange(PROP_title, this.title, this.title = title);
	}

	protected ObservableRecording getObservableRecording() {
		ObservableRecording r = (ObservableRecording) getRecording();
		r.inflate();
		return r;
	}

	public AbstractContributableEntity getContributionFacade() {
		return contributorFacade;
	}

	@Override
	protected void postInflate() {
		super.postInflate();
		contributorFacade = new MyContributorFacade();
	}
}