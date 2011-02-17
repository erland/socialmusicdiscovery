package org.socialmusicdiscovery.rcp.content;

import org.socialmusicdiscovery.server.business.model.core.Medium;

import com.google.gson.annotations.Expose;

public class ObservableMedium extends AbstractObservableEntity<Medium> implements Medium {

	private static final String PROP_number = "number";
	@Expose
	private Integer number;

	@Override
	public Integer getNumber() {
		return this.number;
	}

	public void setNumber(Integer number) {
		firePropertyChange(PROP_number, this.number, this.number = number);
	}

}
