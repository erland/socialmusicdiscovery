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

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

/**
 * <p>
 * The root abstraction of all elements that we edit as part of another element
 * in the client. The class implements a number of interfaces that are typical
 * for "secondary" client objects; these can be deleted, inserted and edited
 * only in association with class objects. They are not editor input elements.
 * </p>
 * 
 * <p>
 * One important purpose with this class is to allow separation of menus;
 * although these objects can be deleted, they are constrained in terms of
 * context; they cannot be deleted anywhere they are seen. Hence, they cannot
 * implement {@link Deletable}.
 * </p>
 * 
 * @author Peer Törngren
 * 
 * @param <T>
 *            the interface the subclass implements
 */
public abstract class AbstractDependentEntity<T extends SMDIdentity> extends AbstractObservableEntity<T> {


	/**
	 * Default implementation calls {@link DataSource#delete(ObservableEntity)}. 
	 * Subclasses should override as necessary. 
	 */
	public void delete() {
		getDataSource().delete(this);
	}
	
}
