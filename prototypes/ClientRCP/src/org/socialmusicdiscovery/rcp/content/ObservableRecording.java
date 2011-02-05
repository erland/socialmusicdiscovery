package org.socialmusicdiscovery.rcp.content;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.socialmusicdiscovery.server.business.model.core.Work;

import com.google.gson.annotations.Expose;

public class ObservableRecording extends AbstractObservableEntity<Recording> implements Recording {

	public static final String PROP_date = "date";
	public static final String PROP_mixOf = "mixOf";
	public static final String PROP_contributors = "contributors";
	public static final String PROP_works = "works";
	
	@Expose private Date date;
	@Expose private Recording mixOf;
	@Expose private Set<Contributor> contributors = new HashSet<Contributor>();
	@Expose private Set<Work> works = new HashSet<Work>();

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public Recording getMixOf() {
		return mixOf;
	}

	@Override
	public Set<Contributor> getContributors() {
		return contributors;
	}

	@Override
	public Set<Work> getWorks() {
		return works;
	}

	@Override
	public void setDate(Date date) {
		firePropertyChange(PROP_date, this.date, this.date = date);
	}

	@Override
	public void setMixOf(Recording mixOf) {
		firePropertyChange(PROP_mixOf, this.mixOf, this.mixOf = mixOf);
	}

	@Override
	public void setContributors(Set<Contributor> contributors) {
		firePropertyChange(PROP_contributors, this.contributors, this.contributors = contributors);
	}

	@Override
	public void setWorks(Set<Work> works) {
		firePropertyChange(PROP_works, this.works, this.works = works);
	}

}
