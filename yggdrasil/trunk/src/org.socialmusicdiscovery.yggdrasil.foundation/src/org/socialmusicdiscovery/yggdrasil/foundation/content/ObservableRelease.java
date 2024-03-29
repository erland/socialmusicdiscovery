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

package org.socialmusicdiscovery.yggdrasil.foundation.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Label;
import org.socialmusicdiscovery.server.business.model.core.Medium;
import org.socialmusicdiscovery.server.business.model.core.RecordingSession;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.yggdrasil.foundation.content.DataSource.Root;
import org.socialmusicdiscovery.yggdrasil.foundation.util.GenericWritableList;
import org.socialmusicdiscovery.yggdrasil.foundation.util.GenericWritableSet;

import com.google.gson.annotations.Expose;

public class ObservableRelease extends AbstractContributableEntity<Release> implements Release {

	public static final String PROP_date = "date"; //$NON-NLS-1$
	public static final String PROP_label = "label"; //$NON-NLS-1$
	public static final String PROP_mediums = "mediums"; //$NON-NLS-1$
	public static final String PROP_tracks = "tracks"; //$NON-NLS-1$
	public static final String PROP_recordingSessions = "recordingSessions"; //$NON-NLS-1$

	@Expose
	private Date date = null;

	@Expose
	private Label label = null;

	@Expose
	private List<Medium> mediums = new ArrayList<Medium>();

	@Expose
	private Set<RecordingSession> recordingSessions = new HashSet<RecordingSession>();

	private transient GenericWritableList<Track> tracks = new GenericWritableList<Track>();

	public ObservableRelease() {
		super(Release.TYPE);
	}

	/**
	 * Must override - see comments on superclass method.
	 */
	@Override
	public GenericWritableSet<Contributor> getContributors() {
		return super.getContributors();
	}

	@Override
	public Date getDate() {
		return date;
	}

	/**
	 * Tracks are not loaded during basic inflate; since not all {@link Track}s
	 * belong to a {@link Release}, we need to query for the {@link Track}s. And
	 * since the {@link Track}s do not have names, we need to inflate the
	 * tracks. And we also want the track numbers etc.
	 */
	@Override
	protected void postInflate() {
		super.postInflate();
		Root<Track> trackRoot = getDataSource().resolveRoot(Track.class);
		Collection<ObservableTrack> allTracks = trackRoot.findAll(this);
		inflateAll(allTracks);
		tracks.addAll(allTracks);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <D extends AbstractDependentEntity> Collection<D> getDeletableDependents() {
		Collection<D> result = new ArrayList(super.getDeletableDependents());
		result.addAll(getTracks());
//		result.addAll(getMediums());
//		result.add(getLabel());
		return result;
	}

	@Override
	public Label getLabel() {
		return label;
	}

	@Override
	public List<Medium> getMediums() {
		return mediums;
	}

	@Override
	public GenericWritableList<Track> getTracks() {
		return tracks;
	}
	
	@Override
	public Set<RecordingSession> getRecordingSessions() {
		return recordingSessions;
	}

	public void setDate(Date date) {
		firePropertyChange(PROP_date, this.date, this.date = date);
	}

	public void setLabel(Label label) {
		System.out.println("ObservableRelease.setLabel(): "+label);
		firePropertyChange(PROP_label, this.label, this.label = label);
	}

	public void setMediums(List<Medium> mediums) {
		firePropertyChange(PROP_mediums, this.mediums, this.mediums = mediums);
	}

	public void setTracks(List<Track> tracks) {
		updateList(PROP_tracks, this.tracks, tracks);
	}

	public void setRecordingSessions(Set<RecordingSession> recordingSessions) {
		firePropertyChange(PROP_recordingSessions, this.recordingSessions, this.recordingSessions = recordingSessions);
	}

}