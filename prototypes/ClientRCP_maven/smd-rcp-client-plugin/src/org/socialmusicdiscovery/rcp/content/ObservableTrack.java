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

import static org.socialmusicdiscovery.rcp.content.ObservableContributor.PROP_artist;
import static org.socialmusicdiscovery.rcp.content.ObservableContributor.PROP_type;
import static org.socialmusicdiscovery.rcp.content.ObservableRecording.PROP_works;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.socialmusicdiscovery.rcp.util.ChangeMonitor;
import org.socialmusicdiscovery.rcp.util.GenericWritableSet;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Medium;
import org.socialmusicdiscovery.server.business.model.core.PlayableElement;
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.socialmusicdiscovery.server.business.model.core.RecordingSession;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.server.business.model.core.Work;

import com.google.gson.annotations.Expose;

public class ObservableTrack extends AbstractDependentEntity<Track> implements Track {
	private class MyTitleManager implements Runnable {

		@Override
		public void run() {
			Recording r = getRecording();
			String t = r==null ? null : r.getName();
			setTitle(t);
		}
	}

	/**
	 * Lots of unsafe types here - see comments on {@link AbstractContributableEntity}.
	 * @param contributables
	 */
	@SuppressWarnings("unchecked")
	private class MyContributorFacade extends AbstractContributableEntity implements Runnable {
		private EffectiveContributorsResolver<ObservableContributor> effectiveContributorsResolver;
		private MyContributorFacade() {
			super(MyContributorFacade.class.getSimpleName()); //s dummy
			update();
			ChangeMonitor.observe(this, ObservableTrack.this, PROP_release, PROP_contributors, PROP_artist, PROP_name);
			ChangeMonitor.observe(this, ObservableTrack.this, PROP_release, PROP_contributors, PROP_type);
			ChangeMonitor.observe(this, ObservableTrack.this, PROP_recording, PROP_contributors, PROP_artist, PROP_name);
			ChangeMonitor.observe(this, ObservableTrack.this, PROP_recording, PROP_contributors, PROP_type);
			ChangeMonitor.observe(this, ObservableTrack.this, PROP_recording, PROP_works, PROP_contributors, PROP_artist, PROP_name);
			ChangeMonitor.observe(this, ObservableTrack.this, PROP_recording, PROP_works, PROP_contributors, PROP_type);
		}

		private void update() {
			List<Collection<ObservableContributor>> orderedContributors = getContributorsInOrderOfPrecedence();
			effectiveContributorsResolver = new EffectiveContributorsResolver<ObservableContributor>(orderedContributors);
			setContributors(effectiveContributorsResolver.getEffectiveContributors());
		}

		@Override
		public void run() {
			update(); // TODO refine, only update the affected set of contributors
		}
		
		private List<Collection<ObservableContributor>> getContributorsInOrderOfPrecedence() {
			List<Collection<ObservableContributor>> result = new ArrayList<Collection<ObservableContributor>>();
			result.add(getRecordingContributors());
			result.add(getWorkContributors());
			result.add(getRecordingSessionContributors());
			result.add(getReleaseContributors());
			return result;
		}

		private Collection<ObservableContributor> getWorkContributors() {
			Set<ObservableContributor> result = new HashSet<ObservableContributor>();
			for (Work w : getRecording().getWorks()) {
				result.addAll(compile(w));
			}
			return result;
		}

		private Collection<ObservableContributor> getReleaseContributors() {
			return compile(getRelease());
		}

		private Collection<ObservableContributor> getRecordingSessionContributors() {
			return compile(resolveRecordingSession(getRecording()));
		}

		private Collection<ObservableContributor> getRecordingContributors() {
			return compile(getRecording());
		}

		private Collection<ObservableContributor> compile(SMDIdentity entity) {
			Set result = new HashSet();
			if (entity!=null) {
				AbstractContributableEntity c = (AbstractContributableEntity) entity;
				Set<Contributor> contributors = c.getContributors();
				for (Contributor contributor: contributors) {
					result.add(contributor);
				}
			}
			return result;
		}

		/**
		 * FIXME stub - should move to some utility method or something?
		 * @param recording
		 * @return <code>null</code>
		 */
		private  RecordingSession resolveRecordingSession(Recording recording) {
			return null; // getRecordingSession();
		}

		@Override
		public IObservableSet getContributors() {
			return ObservableTrack.this.getContributors();
		}

		/**
		 * Do <b>NOT</b> call superclass; we must fire event even if no elements have changed since 
		 * we need to update if a contributor name has changed. Also, we do <b>NOT</b> thus instance 
		 * to become dirty because of a change in a derived property.  
		 */
		@Override
		public void setContributors(Set newContributors) {
			ObservableTrack.this.setDirtyEnabled(false);
			IObservableSet myContributors = ObservableTrack.this.getContributors();
			SetDiff diff = Diffs.computeSetDiff(myContributors, newContributors);
			myContributors.removeAll(diff.getRemovals());
			myContributors.addAll(diff.getAdditions());
			ObservableTrack.this.firePropertyChange(PROP_contributors); // "refresh event"
			ObservableTrack.this.setDirtyEnabled(true);
		}

	}

	public static final String PROP_number = "number";
	public static final String PROP_playableElements = "playableElements";
	public static final String PROP_medium = "medium";
	public static final String PROP_recording = "recording";
	public static final String PROP_release = "release";
	public static final String PROP_title = "title";
	public static final String PROP_contributors = "contributors";

	private transient String title;
	private transient final MyTitleManager titleManager = new MyTitleManager();
	
	@Expose private Integer number;
	@Expose private Medium medium;
	@Expose private Recording recording;
	@Expose private Set<PlayableElement> playableElements = new HashSet<PlayableElement>();
	@Expose private Release release;
	private MyContributorFacade contributorFacade;
	private GenericWritableSet<Contributor> contributors = new GenericWritableSet<Contributor>();

	@Override
	public Integer getNumber() {
		return number;
	}

	/**
	 * @return
	 */
	public IObservableSet getContributors() {
		return contributors;
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
		updateSet(PROP_playableElements, this.playableElements, playableElements);
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
		return title;
	}

	public void setTitle(String title) {
		firePropertyChange(PROP_title, this.title, this.title = title);
	}

	public AbstractContributableEntity getContributionFacade() {
		return contributorFacade;
	}

	/**
	 * Since title is derived from the name of the {@link Recording}, 
	 * and since that in its turn is (or can be) derived from its {@link Work},
	 * we need to inflate the {@link Recording}. 
	 */
	@Override
	protected void postInflate() {
		super.postInflate();
		ObservableRecording r = (ObservableRecording) getRecording();
		if (r!=null) {
			r.inflate();
		}
		hookTitleManager();
		hookContributorsListener();
	}

	@Override
	protected void postCreate() {
		super.postCreate();
		hookTitleManager();
		hookContributorsListener();
	}

	private void hookContributorsListener() {
		contributorFacade = new MyContributorFacade();
	}

	private void hookTitleManager() {
		titleManager.run();
		ChangeMonitor.observe(titleManager, this, PROP_recording, PROP_name);
	}

	/**
	 * Is the supplied contributor an "effective contributor", or is it
	 * overridden/disabled by some other?
	 * 
	 * @param contributor
	 */
	@SuppressWarnings("unchecked")
	public boolean isEffectiveContributor(Contributor contributor) {
		for (Contributor c : (Collection<Contributor>) getContributors()) {
			if (c.equals(contributor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void delete() {
		inflate();
		ObservableRecording recording = (ObservableRecording) getRecording();
		if (recording.isInflated()) {
			recording.getTracks().remove(this);  
		}

		ObservableRelease release = (ObservableRelease) getRelease();
		if (release.isInflated()) {
			release.getTracks().remove(this);
		}
		
		super.delete();
	}
	
}