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

import java.util.Collection;

import org.socialmusicdiscovery.server.business.model.core.Contributor;

/**
 * An instance that can delete itself and all affected (dependent) instances,
 * probably by notifying dependent elements and delegating the actual suicide to
 * the {@link DataSource}.
 * 
 * @author Peer Törngren
 */
public interface Deletable {

	/**
	 * Delete this instance. Fire events and take necessary actions to ensure
	 * that the model stays consistent and all listeners are notified. The
	 * recommended approach is that "core" entities delete themselves and all
	 * dependents by calling {@link DataSource#delete(ObservableEntity)}, and
	 * that dependent instances (like a {@link Contributor}) asks its owner to
	 * be removed from the concerned property (typically a collection of some
	 * sort).
	 */
	void delete();

	/**
	 * Get other {@link Deletable} elements that must be deleted with this
	 * instance, in deletion order (if it matters).
	 * 
	 * @return Collection
	 */
	<T extends Deletable> Collection<T> getDependentsToDelete();	
}