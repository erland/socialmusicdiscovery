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

package org.socialmusicdiscovery.yggdrasil.foundation.util;

import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.yggdrasil.foundation.Activator;
import org.socialmusicdiscovery.yggdrasil.foundation.content.DataSource;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableEntity;

/**
 * Some SMD-specific convenience utils.
 * 
 * @author Peer Törngren
 *
 */
public class SMDUtil {

	private SMDUtil() {}

	/**
	 * Convenience method.
	 * @return {@link DataSource}
	 */
	public static DataSource getDataSource() {
		return Activator.getDefault().getDataSource();
	}

	/**
	 * Analyze supplied element and return a string to represent the content type.
	 * This type can be used as the "filename" in a content type extension, and thus 
	 * editors can be mapped to this type id. Typically, an SMD {@link Artist} would return the 
	 * string "Artist".
	 *  
	 * @param element
	 * @return Simple unqualified string or <code>null</code> 
	 */
	public static String resolveContentTypeName(Object element) {
		return element instanceof ObservableEntity ? ((ObservableEntity) element).getTypeName() : null;
	}
	

}
