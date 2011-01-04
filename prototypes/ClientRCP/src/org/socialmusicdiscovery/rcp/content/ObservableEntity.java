package org.socialmusicdiscovery.rcp.content;

import org.eclipse.ui.IEditorInput;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;

/**
 * A {@link ModelObject} that can be edited in an editor.
 * 
 * @author Peer Törngren
 *
 */
public interface ObservableEntity extends IEditorInput, ModelObject, SMDIdentity {
	
	String PROP_dirty = "dirty"; //$NON-NLS-1$
	
	/**
	 * Does this instance have unsaved changes?
	 * 
	 * @return boolean
	 */
	boolean isDirty();

}
