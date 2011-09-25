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

package org.socialmusicdiscovery.yggdrasil.foundation.content;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.yggdrasil.foundation.content.AbstractContributableEntity;
import org.socialmusicdiscovery.yggdrasil.foundation.content.DataSource;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableArtist;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableContributor;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableRecording;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableRelease;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableTrack;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableWork;
import org.socialmusicdiscovery.yggdrasil.foundation.test.FakeDataSource;
import org.socialmusicdiscovery.yggdrasil.foundation.test.MultiPurposeListener;
import org.socialmusicdiscovery.yggdrasil.foundation.test.TestRealm;

/**
 * Abstract test case for UI-related testing; sets up and tears down a
 * {@link Realm} to enable jface data binding.
 * 
 * @author Peer Törngren
 * 
 */
public abstract class AbstractTestCase extends TestCase {
	
	protected static final String CONDUCTOR = "conductor";
	protected static final String COMPOSER = "composer";
	protected static final String PRODUCER = "producer";
	protected static final String PERFORMER = "performer";
	protected static final String LYRICS = "lyrics";
	
	private TestRealm realm;

	protected DataSource dataSource;
	protected MultiPurposeListener listener;
	
	protected ObservableArtist artist;
	protected ObservableRelease release;
	protected ObservableTrack track;
	protected ObservableRecording recording;
	protected ObservableWork work1;
	protected ObservableWork work2;
	private int id = 0;
	

	/**
	 * Creates a new default realm for every test.
	 */
	protected void setUp() throws Exception {
		super.setUp();
		realm = new TestRealm();
		dataSource = new FakeDataSource();
		listener = new MultiPurposeListener();
		
	}

	/**
	 * Removes the default realm.
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		realm.dispose();
	}
	
	protected ObservableTrack track(int trackNumber, ObservableRelease release, ObservableRecording recording) {
		ObservableTrack t = new ObservableTrack();
		t.setTestDataSource(dataSource);
		t.setId(String.valueOf(trackNumber));
		t.setNumber(Integer.valueOf(trackNumber));
		t.setRelease(release);
		t.setRecording(recording);
		t.postCreate();
		t.setDirty(false);
		
		release.setTracks(Arrays.asList((Track)t));
		recording.setTracks(Arrays.asList(t));
	
		return t;
	}

	protected ObservableRelease release() {
		ObservableRelease r = new ObservableRelease();
		r.setId(nextId());
		r.setTestDataSource(dataSource);
		r.postCreate();
		r.setDirty(false);
		return r;
	}

	protected ObservableArtist artist() {
		return artist(id++, "artist1");
	}
	
	protected ObservableArtist artist(int id, String name) {
		ObservableArtist a = new ObservableArtist();
		a.setTestDataSource(dataSource);
		a.setId(String.valueOf(id));
		a.setName(name);
		a.postCreate();
		a.setContributions(Collections.<ObservableContributor>emptySet());
		return a;
	}

	protected ObservableRecording recording() {
		int nextId = id++;
		return recording(nextId, "Recording#"+nextId);
	}
	protected ObservableRecording recording(int id, String name) {
		ObservableRecording r = new ObservableRecording();
		r.setTestDataSource(dataSource);
		r.setId(String.valueOf(id));
		r.setName(name);
		r.postCreate();
		return r;
	}

	protected ObservableWork work(int id, String name) {
		ObservableWork w = new ObservableWork();
		w.setTestDataSource(dataSource);
		w.setId(String.valueOf(id));
		w.setName(name);
		return w;
	}

	protected ObservableContributor contributor(AbstractContributableEntity e, ObservableArtist a, String role) {
		ObservableContributor c = new ObservableContributor();
		c.setTestDataSource(dataSource);
		c.setId(nextId());
		c.setArtist(a);
		c.setType(role);
		c.setOwner(e);
		c.postCreate();
		return c;
	}

	@SuppressWarnings("unchecked")
	protected Set asSet(Object... elements) {
		return new HashSet(Arrays.asList(elements));
	}

	protected MultiPurposeListener listener(IObservableSet set) {
		MultiPurposeListener listener = new MultiPurposeListener();
		set.addSetChangeListener(listener);
		return listener;
	}
	
	protected MultiPurposeListener listener(IObservableList list) {
		MultiPurposeListener listener = new MultiPurposeListener();
		list.addListChangeListener(listener);
		return listener;
	}
	private String nextId() {
		return String.valueOf(id++);
	}
}
