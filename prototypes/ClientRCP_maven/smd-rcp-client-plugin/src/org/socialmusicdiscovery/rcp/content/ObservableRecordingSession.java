package org.socialmusicdiscovery.rcp.content;

import java.util.Date;
import java.util.Set;

import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.socialmusicdiscovery.server.business.model.core.RecordingSession;

import com.google.gson.annotations.Expose;

public class ObservableRecordingSession extends AbstractObservableEntity<RecordingSession> implements RecordingSession {

	public static final String PROP_date = "date";
	public static final String PROP_contributors = "contributions";
	public static final String PROP_recordings = "recordings";
	
	@Expose private Date date;
	@Expose private Set<Contributor> contributors;
	@Expose private Set<Recording> recordings;

	@Override
	public Date getDate() {
		return date;
	}
	
	@Override
	public Set<Contributor> getContributors() {
		return contributors;
	}

	@Override
	public Set<Recording> getRecordings() {
		return recordings;
	}

	public void setDate(Date date) {
		firePropertyChange(PROP_date, this.date, this.date = date);
	}

	public void setContributors(Set<Contributor> contributors) {
		firePropertyChange(PROP_contributors, this.contributors, this.contributors = contributors);
	}

	public void setRecordings(Set<Recording> recordings) {
		firePropertyChange(PROP_recordings, this.recordings, this.recordings = recordings);
	}

}
