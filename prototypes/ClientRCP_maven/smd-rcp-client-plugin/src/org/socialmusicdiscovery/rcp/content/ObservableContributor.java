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
import java.beans.PropertyChangeListener;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Contributor;

import com.google.gson.annotations.Expose;

public class ObservableContributor extends AbstractDependentEntity<Contributor> implements Contributor {

	/**
	 * Listen to my own property change events to update affected
	 * {@link ObservableArtist} whenever the artist property changes. We
	 * could do this as part of the setter, but using a separate listener lets
	 * us control if and when to update dependencies; if we have no listeners,
	 * the setter is free of side effects.
	 */
	private class MyArtistChangeListener implements PropertyChangeListener {
		public MyArtistChangeListener(ObservableArtist artist) {
			addTo(artist);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			removeFrom((ObservableArtist) evt.getOldValue());
			addTo((ObservableArtist) evt.getNewValue());
		}

		private void removeFrom(ObservableArtist artist) {
			if (artist!=null && artist.isContributionsLoaded()) {
				boolean removed = artist.getContributions().remove(ObservableContributor.this);
				assert removed : "Not removed from recording: "+artist;
			}
		}

		private void addTo(ObservableArtist artist) {
			if (artist!=null && artist.isContributionsLoaded()) {
				boolean added = artist.getContributions().add(ObservableContributor.this);
				assert added : "Not added to recording: "+artist;
			}
		}
	}

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
	
	/**
	 * Constructor for new instances created by client.
	 */
	public ObservableContributor(Contributor template) {
		this.owner = (AbstractContributableEntity) template.getOwner();
		this.type = template.getType();
		this.artist = template.getArtist();
		postCreate();
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
		assert artist==null || artist instanceof ObservableArtist : "Not an "+ObservableArtist.class+": "+artist;
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
		if (getOwner().isInflated()) {
			getOwner().getContributors().remove(this);
		}
		if (getArtist().isContributionsLoaded()) {
			getArtist().getContributions().remove(this);
		}
		super.delete();
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
	protected void postInflate() {
		super.postInflate();
		hookListeners();
	}

	@Override
	protected void postCreate() {
		super.postCreate();
		if (getOwner()!=null && getOwner().isInflated()) {
			getOwner().getContributors().add(this);
		}
		hookListeners();
	}
	
	private void hookListeners() {
		addPropertyChangeListener(PROP_artist, new MyArtistChangeListener(getArtist()));
	}

}