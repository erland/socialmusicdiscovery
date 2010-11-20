package org.socialmusicdiscovery.rcp.views.navigator;

import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.socialmusicdiscovery.rcp.content.DataSource;
import org.socialmusicdiscovery.rcp.content.DataSource.Root;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;

class NavigatorTreeContentProvider implements ITreeContentProvider {
	private static final Object[] NONE = {};
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
	public void dispose() {
	}
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof DataSource) {
			return ((DataSource)parentElement).getRoots();
		} else if (parentElement instanceof Root) {
			return toArray(((Root<?>)parentElement).findAll());
		} else if (parentElement instanceof Artist) {
			return NONE;
		} else if (parentElement instanceof Release) {
			return toArray(((Release)parentElement).getTracks());
		} else if (parentElement instanceof Track) {
			return NONE;
		}
		throw new IllegalArgumentException("Unknown type: "+parentElement);
	}
	private Object[] toArray(Collection<?> all) {
		return all.toArray(new Object[all.size()]);
	}
	
	public Object getParent(Object element) {
		return null;
	}
	public boolean hasChildren(Object element) {
		// if not loaded, we're lying thru our teeth
		// would like to answer "maybe" :-)
		return element instanceof Root ? ((Root<?>)element).hasChildren() : getChildren(element).length > 0;
	}
}