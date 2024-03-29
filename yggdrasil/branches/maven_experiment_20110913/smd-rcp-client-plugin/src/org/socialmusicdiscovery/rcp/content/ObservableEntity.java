/*
 *  Copyright 2010-2011, Social Music Discovery project
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of Social Music Discovery project nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.socialmusicdiscovery.rcp.content;

import java.beans.PropertyChangeEvent;
import java.util.Set;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

/**
 * A {@link ModelObject} that can be edited in an editor.
 * 
 * @author Peer Törngren
 *
 */
public interface ObservableEntity<T extends SMDIdentity> extends ModelObject, Comparable<ModelObject>, SMDIdentity {
	
	String PROP_id = "id"; //$NON-NLS-1$
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

	/**
	 * Get entities that should be deleted when this instance is saved.
	 * @return {@link Set} (possibly empty)
	 */
	Set<? extends ObservableEntity> getRemovedDependents();

	/**
	 * Get entities that should be saved when this instance is saved.
	 * @return {@link Set} (possibly empty)
	 */
	Set<? extends ObservableEntity> getSaveableDependents();

	/**
	 * Get the simple (unqualified) name of the root type that this entity implements.
	 * This string can be used as a key to render strings and icons in label providers etc.
	 * 
	 * @return String representing the simple name of an interface that extends {@link SMDIdentity}
	 * @see Class#getSimpleName()  
	 */
	String getTypeName();
}
