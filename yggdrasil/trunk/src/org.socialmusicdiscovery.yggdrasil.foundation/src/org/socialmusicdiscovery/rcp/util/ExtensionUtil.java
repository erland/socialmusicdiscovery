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

package org.socialmusicdiscovery.rcp.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.socialmusicdiscovery.rcp.dialogs.EditorDialog;
import org.socialmusicdiscovery.rcp.error.FatalApplicationException;


/**
 * Some helpers for working with extensions, both standard Eclipse and Yggdrasil
 * extension points.
 * 
 * @author Peer Törngren
 * 
 */
public final class ExtensionUtil {

	private ExtensionUtil() {}

	public static <T extends EditorDialog> T resolveEditorDialog(String contentName) {
		String contentTypeId = resolveContentTypeId(contentName);
		IConfigurationElement[] extensions = Platform.getExtensionRegistry().getConfigurationElementsFor(EditorDialog.ExtensionID);
		
		for (IConfigurationElement extension : extensions) {
			if (extension.getAttribute("contentTypeId").equals(contentTypeId)) { //$NON-NLS-1$
				try {
					return (T) extension.createExecutableExtension("class"); //$NON-NLS-1$
				} catch (CoreException e1) {
					throw new FatalApplicationException("Unable to instantiate editor dialog: "+extension.getName(), e1);  //$NON-NLS-1$
				}
			}
		}
		return null;
	}

	private static String resolveContentTypeId(String contentName) {
		return Platform.getContentTypeManager().findContentTypeFor(contentName).getId();
	}


}
