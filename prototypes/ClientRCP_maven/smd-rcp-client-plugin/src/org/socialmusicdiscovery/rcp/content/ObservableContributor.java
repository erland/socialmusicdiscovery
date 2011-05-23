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

import org.socialmusicdiscovery.rcp.util.NotYetImplemented;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Contributor;

import com.google.gson.annotations.Expose;

public class ObservableContributor extends AbstractObservableEntity<Contributor> implements Contributor {

	public static final String PROP_owner = "owner";
	public static final String PROP_artist = "artist";
	public static final String PROP_type = "type";
	
	@Expose private Artist artist;
	@Expose private String type;
	@Expose private AbstractContributableEntity owner;

	/**
	 * Default constructor.
	 */
	public ObservableContributor() {
	}
	
	@Override
	public String getType() {
		return type;
	}

	@Override
	public ObservableArtist getArtist() {
		return (ObservableArtist) artist;
	}

	public void setArtist(Artist artist) {
		assert artist instanceof ObservableArtist : "Not an "+ObservableArtist.class+": "+artist;
		firePropertyChange(PROP_artist, this.artist, this.artist = artist);
	}

	public void setType(String type) {
		firePropertyChange(PROP_type, this.type, this.type = type);
	}

	@Override
	public String toString() {
		String artistName = getArtist()==null ? "?" : getArtist().getName();
		String entityName = getOwner()==null ? "?" : getOwner().getName();
		return getClass().getSimpleName()+"@"+hashCode()+"/"+entityName+"-"+getType()+":"+artistName + " ("+getName()+")";
	}

	@Override
	public void delete() {
		AbstractContributableEntity owner = getOwner();
		if (owner.isInflated()) {
			owner.getContributors().remove(this);
		}
		if (getArtist().isContributionsLoaded()) {
			getArtist().getContributions().remove(this);
		}
		super.delete();
	}

	@Override
	public Contributor newInstance() {
		NotYetImplemented.openDialog("Cannot yet create "+getClass().getSimpleName());
		return null;
	}
	
	@Override
	public AbstractContributableEntity getOwner() {
		return owner;
	}

	@Override
	public void setOwner(SMDIdentity owner) {
		firePropertyChange(PROP_owner, this.owner, this.owner = (AbstractContributableEntity) owner);
	}

	@Override
	protected void postCreate() {
		super.postCreate();
		if (getOwner().isInflated()) {
			getOwner().getContributors().add(this);
		}
		if (getArtist().isContributionsLoaded()) {
			getArtist().getContributions().add(this);
		}
	}

}