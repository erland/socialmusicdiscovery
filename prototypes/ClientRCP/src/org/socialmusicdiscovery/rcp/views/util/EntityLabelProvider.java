package org.socialmusicdiscovery.rcp.views.util;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;

public class EntityLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		return super.getImage(element);
	}

	@Override
	public String getText(Object element) {
		return element instanceof SMDEntity ? getText((SMDEntity<?>) element) : super.getText(element);
	}

	public static String getText(SMDEntity<?> entity) {
		if (entity instanceof Artist) {
			return ((Artist)entity).getName();
		} else 	if (entity instanceof Release) {
			return ((Release)entity).getName();
		} else 	if (entity instanceof Track) {
			Track track = (Track)entity;
			Integer number = track.getNumber();
			String name = track.getRecording().getName();
			return number + "-" + name;
		}
		throw new IllegalArgumentException("Unexpected entity: "+entity);
	}

}
