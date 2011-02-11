package org.socialmusicdiscovery.rcp.content;

import java.beans.PropertyChangeEvent;

import org.eclipse.ui.IEditorInput;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;

/**
 * A {@link ModelObject} that can be edited in an editor.
 * 
 * @author Peer Törngren
 *
 */
public interface ObservableEntity<T extends SMDIdentity> extends IEditorInput, ModelObject, SMDIdentity {
	
	String PROP_dirty = "dirty"; //$NON-NLS-1$
	
	/**
	 * Does this instance have unsaved changes?
	 * 
	 * @return boolean
	 */
	boolean isDirty();

	/**
	 * Update the dirty status. Set to <code>true</code> when changes are made,
	 * set to <code>false</code> when changes are saved to persistent store or
	 * canceled ("undo"). Method must be called whenever the persistent state of
	 * this instance changes. Implementers must fire a {@link PropertyChangeEvent}
	 * for {@value #PROP_dirty}.
	 * 
	 * @param isDirty
	 */
	void setDirty(boolean isDirty);

}
