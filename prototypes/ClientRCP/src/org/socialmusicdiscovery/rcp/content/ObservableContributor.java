package org.socialmusicdiscovery.rcp.content;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Contributor;

public class ObservableContributor extends AbstractObservableEntity<Contributor> implements Contributor {

//	public ObservableContributor(Contributor shallowEntity) {
//		super(shallowEntity);
//	}

	@Override
	public IObservableList getObservableChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setType(String type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Artist getArtist() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setArtist(Artist artist) {
		// TODO Auto-generated method stub
		
	}

}
