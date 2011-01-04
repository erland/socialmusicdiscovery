package org.socialmusicdiscovery.rcp.content;

import java.util.Set;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Person;

public class ObservableArtist extends AbstractObservableEntity<Artist> implements Artist {

	@Override
	public IObservableList getObservableChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Person getPerson() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPerson(Person person) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Artist> getAliases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAliases(Set<Artist> aliases) {
		// TODO Auto-generated method stub
		
	}

}
