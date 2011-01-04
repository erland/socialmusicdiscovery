package org.socialmusicdiscovery.rcp.content;

import java.util.Set;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.socialmusicdiscovery.server.business.model.core.Medium;
import org.socialmusicdiscovery.server.business.model.core.PlayableElement;
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;

public class ObservableTrack extends AbstractObservableEntity<Track> implements Track {

//	public ObservableTrack(Track shallowEntity) {
//		super(shallowEntity);
//	}

	@Override
	public IObservableList getObservableChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNumber(Integer number) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Medium getMedium() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMedium(Medium medium) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Recording getRecording() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRecording(Recording recording) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<PlayableElement> getPlayableElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPlayableElements(Set<PlayableElement> playableElements) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Release getRelease() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRelease(Release release) {
		// TODO Auto-generated method stub
		
	}

}