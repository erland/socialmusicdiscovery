package org.socialmusicdiscovery.rcp.content;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Work;

import com.google.gson.annotations.Expose;

public class ObservableWork extends AbstractObservableEntity<Work> implements Work {

	public static final String PROP_date = "date";
	public static final String PROP_parts = "parts";
	public static final String PROP_parent = "parent";
	public static final String PROP_contributors = "contributors";
	
	@Expose private Date date;
	@Expose private Set<Work> parts = new HashSet<Work>();
	@Expose private Work parent;
	@Expose private Set<Contributor> contributors = new HashSet<Contributor>();
	
	@Override
	public Date getDate() {
		return date;
	}
	@Override
	public Set<Work> getParts() {
		return parts;
	}

	@Override
	public Work getParent() {
		return parent;
	}
	@Override
	public Set<Contributor> getContributors() {
		return contributors;
	}
	public void setDate(Date date) {
		firePropertyChange(PROP_date, this.date, this.date = date);
	}
	public void setParts(Set<Work> parts) {
		firePropertyChange(PROP_parts, this.parts, this.parts = parts);
	}
	public void setParent(Work parent) {
		firePropertyChange(PROP_parent, this.parent, this.parent = parent);
	}
	public void setContributors(Set<Contributor> contributors) {
		firePropertyChange(PROP_contributors, this.contributors, this.contributors = contributors);
	}

}
