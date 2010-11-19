package org.socialmusicdiscovery.rcp.views.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.socialmusicdiscovery.rcp.editors.artist.ArtistEditor;
import org.socialmusicdiscovery.rcp.editors.release.ReleaseEditor;
import org.socialmusicdiscovery.rcp.util.WorkbenchUtil;
import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Release;

public class OpenListener implements IOpenListener {
	private final Map<Class<?>, String> views = new HashMap<Class<?>, String>();
	private final Map<Class<?>, String> editors = new HashMap<Class<?>, String>();

	public OpenListener() {
		editors.put(Artist.class, ArtistEditor.ID);
		editors.put(Release.class, ReleaseEditor.ID);
	}


	@Override
	public void open(OpenEvent event) {
		SMDEntity<?> entity = getSelectedEntity(event);
		
		String editorId = resolveEditorId(entity);
		String viewId = resolveViewId(entity);
		
		if (editorId!=null) {
			WorkbenchUtil.openEditor(entity, editorId);
		} else {
			if (viewId!=null) {
				WorkbenchUtil.openView(viewId, entity.getId());
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private SMDEntity getSelectedEntity(OpenEvent event) {
		StructuredSelection selection = (StructuredSelection) event.getSelection();
		Object selected = selection.getFirstElement();
		return selected instanceof SMDEntity ? (SMDEntity) selected : null;
	}

	private String resolveViewId(Object element) {
		// TODO this will need to be made much more robust, we cannot depend on the base class
		// the view should probably declare an interface that it can handle 
		return views.get(element.getClass());
	}

	private String resolveEditorId(Object element) {
		// TODO this will need to be made much more robust, we cannot depend on the base class
		// the editor should probably declare an interface that it can handle 
		return editors.get(element.getClass());
	}
}
