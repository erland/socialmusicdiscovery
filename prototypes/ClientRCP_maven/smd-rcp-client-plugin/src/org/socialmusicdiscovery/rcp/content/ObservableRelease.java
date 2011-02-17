package org.socialmusicdiscovery.rcp.content;

import java.util.ArrayList;
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

import com.google.gson.annotations.Expose;

public class ObservableRelease extends AbstractObservableEntity<Release> implements Release {

	public static final String PROP_date = "date"; //$NON-NLS-1$
	public static final String PROP_label = "label"; //$NON-NLS-1$
	public static final String PROP_mediums = "mediums"; //$NON-NLS-1$
	public static final String PROP_tracks = "tracks"; //$NON-NLS-1$
	public static final String PROP_recordingSessions = "recordingSessions"; //$NON-NLS-1$
	public static final String PROP_contributors = "contributors"; //$NON-NLS-1$

	@Expose
	private Date date = null;

	@Expose
	private Label label = null;

	@Expose
	private List<Medium> mediums = new ArrayList<Medium>();

	@Expose
	private List<Track> tracks = new ArrayList<Track>();

	@Expose
	private Set<RecordingSession> recordingSessions = new HashSet<RecordingSession>();

	@Expose
	private Set<Contributor> contributors = new HashSet<Contributor>();

	@Override
	public Date getDate() {
		return date;
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
	public List<Track> getTracks() {
		return tracks;
	}

	@Override
	public Set<RecordingSession> getRecordingSessions() {
		return recordingSessions;
	}

	@Override
	public Set<Contributor> getContributors() {
		return contributors;
	}

	public void setDate(Date date) {
		firePropertyChange(PROP_date, this.date, this.date = date);
	}

	public void setLabel(Label label) {
		firePropertyChange(PROP_label, this.label, this.label = label);
	}

	public void setMediums(List<Medium> mediums) {
		firePropertyChange(PROP_mediums, this.mediums, this.mediums = mediums);
	}

	public void setTracks(List<Track> tracks) {
		firePropertyChange(PROP_tracks, this.tracks, this.tracks = tracks);
	}

	public void setRecordingSessions(Set<RecordingSession> recordingSessions) {
		firePropertyChange(PROP_recordingSessions, this.recordingSessions, this.recordingSessions = recordingSessions);
	}

	public void setContributors(Set<Contributor> contributors) {
		firePropertyChange(PROP_contributors, this.contributors, this.contributors = contributors);
	}
}