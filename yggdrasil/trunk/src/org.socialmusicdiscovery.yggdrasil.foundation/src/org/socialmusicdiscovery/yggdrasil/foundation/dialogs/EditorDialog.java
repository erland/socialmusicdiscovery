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

package org.socialmusicdiscovery.yggdrasil.foundation.dialogs;

import org.eclipse.ui.IEditorInput;
import org.socialmusicdiscovery.yggdrasil.foundation.Activator;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableEntity;
import org.socialmusicdiscovery.yggdrasil.foundation.util.WorkbenchUtil;

/**
 * An "editor" for modifying existing instances, typically implemented as a
 * modal dialog. This is primarily useful for editing dependent entities that do
 * not implement {@link IEditorInput} and hence cannot be edited in a regular
 * editor. Implementers of this interface should typically be registered with
 * the extension point along with a declaration of the content type the dialog
 * handles. This way, the dialog can be "automagically" launched on selected
 * content by calling {@link WorkbenchUtil#openDistinct(Object)}, the same way
 * we open editors for {@link IEditorInput} elements.
 * 
 * @author Peer Törngren
 * 
 */
public interface EditorDialog<T extends ObservableEntity> {

	/**
	 * The ID of the editor dialog extension point, as declared in the plug-in
	 * metadata.
	 */
	String ExtensionID = Activator.PLUGIN_ID+".editordialogs";


	/**
	 * Edit the supplied entity and save it to persistent store. 
	 *  
	 * @param <code>true</code> if entity was edited, <code>false</code> if user did not make any changes
	 */
	boolean edit(T entity);

}
