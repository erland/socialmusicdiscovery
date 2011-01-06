package org.socialmusicdiscovery.rcp.content;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Person;

import com.google.gson.annotations.Expose;

public class ObservableArtist extends AbstractObservableEntity<Artist> implements Artist {

	public static final String PROP_person = "person";
	public static final String PROP_aliases = "aliases";
	
	@Expose private Person person;
	@Expose private Set<Artist> aliases = new HashSet<Artist>();

	@Override
	public IObservableList getObservableChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Person getPerson() {
		return person;
	}

	@Override
	public Set<Artist> getAliases() {
		return aliases ;
	}

	public void setPerson(Person person) {
		firePropertyChange(PROP_person, this.person, this.person = person);
	}

	public void setAliases(Set<Artist> aliases) {
		firePropertyChange(PROP_aliases, this.aliases, this.aliases = aliases);
	}

}
