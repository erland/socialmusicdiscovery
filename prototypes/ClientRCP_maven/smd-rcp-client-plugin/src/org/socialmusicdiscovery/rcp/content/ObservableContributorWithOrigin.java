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

import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Contributor;

/**
 * A wrapper to allow filtering on where a {@link Contributor} is defined.
 * 
 * @author Peer Törngren
 * 
 */
public class ObservableContributorWithOrigin extends ObservableContributor implements Contributor {
	
	private final Class origin;
	private final Contributor wrappedContributor;

	public ObservableContributorWithOrigin(Class origin, Contributor contributor) {
		this.origin = origin;
		this.wrappedContributor = contributor;
	}

	public Class getOrigin() {
		return origin;
	}

	public Artist getArtist() {
		return wrappedContributor.getArtist();
	}

	public String getId() {
		return wrappedContributor.getId();
	}

	public String getType() {
		return wrappedContributor.getType();
	}

	public void setArtist(Artist arg0) {
		wrappedContributor.setArtist(arg0);
	}

	public void setId(String arg0) {
		wrappedContributor.setId(arg0);
	}

	public void setType(String arg0) {
		wrappedContributor.setType(arg0);
	}

	public Contributor getWrappedContributor() {
		return wrappedContributor;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+"@"+hashCode()+"-"+getType()+":"+getArtist().getName();
	}

}
