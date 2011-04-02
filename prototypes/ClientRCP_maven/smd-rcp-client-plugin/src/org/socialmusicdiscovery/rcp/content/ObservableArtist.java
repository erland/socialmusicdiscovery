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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Person;

import com.google.gson.annotations.Expose;

public class ObservableArtist extends AbstractObservableEntity<Artist> implements Artist {

	public static final String PROP_person = "person";
	public static final String PROP_aliases = "aliases";
	public static final String PROP_contributions = "contributions";
	
	@Expose private Person person;
	@Expose private Set<Artist> aliases = new HashSet<Artist>();
	private transient Set<ObservableContribution> contributions;

	@Override
	public Person getPerson() {
		return person;
	}

	@Override
	public Set<Artist> getAliases() {
		return aliases ;
	}

	public void setPerson(Person person) {
		firePropertyChange(PROP_person, this.person, this.person = person);
	}

	public void setAliases(Set<Artist> aliases) {
		firePropertyChange(PROP_aliases, this.aliases, this.aliases = aliases);
	}

	public Set<ObservableContribution> getContributions() {
		if (contributions==null) {
			contributions = resolveContributions();
		}
		return contributions;
	}

	public void setContributions(Set<ObservableContribution> contributions) {
		if (this.contributions==null) {
			this.contributions = new HashSet<ObservableContribution>();
		}
		updateSet(PROP_contributions, this.contributions, contributions);
	}

	@SuppressWarnings("unchecked")
	private Set<ObservableContribution> resolveContributions() {
		return Collections.EMPTY_SET; // FIXME resolve artist contributions
	}

}
