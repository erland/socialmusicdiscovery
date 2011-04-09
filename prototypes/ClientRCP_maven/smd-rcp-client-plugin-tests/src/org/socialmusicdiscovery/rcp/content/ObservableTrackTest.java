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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.socialmusicdiscovery.rcp.test.AbstractTestCase;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Track;

/**
 * @author Peer Törngren
 */
@SuppressWarnings("unchecked")
public class ObservableTrackTest extends AbstractTestCase {

	private class MyPropertyChangeListener implements PropertyChangeListener {

		private boolean isChanged = false;

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			this.isChanged = true;
		}

		public boolean isChanged() {
			boolean result = isChanged;
			isChanged = false;
			return result;
		}

	}

	private static final String CONDUCTOR = "conductor";
	private static final String COMPOSER = "composer";
	private static final String PRODUCER = "producer";
	private static final String PERFORMER = "performer";
	private static final String LYRICS = "lyrics";
	
	private Set<Contributor> releaseContributors;
	private ObservableRelease release;
	private ObservableArtist releaseArtist;
	private ObservableTrack track;
	private ObservableRecording recording;
	private ObservableContributor releasePerformer;
	private ObservableWork work1;
	private ObservableWork work2;
	private ObservableArtist recordingArtist;
	private ObservableArtist workArtist1;
	private ObservableContributor recordingProducer;
	private Set recordingContributors;
	private ObservableContributor workComposer1;
	private Set workContributors1;
	private ObservableContributor workAuthor2;
	private Set workContributors2;
	private MyPropertyChangeListener listener;
	private ObservableArtist workArtist2;

	public void setUp() throws Exception {
		super.setUp();
		
		listener = new MyPropertyChangeListener();
		
		// artists
		releaseArtist = artist(1, "releaseArtist");
		recordingArtist = artist(2, "recordingArtist");
		workArtist1 = artist(3, "workArtist1");
		workArtist2 = artist(4, "workArtist2");
		
		// recording and works
		recording = recording(1, null);
		work1 = work(1, "work1");
		work2 = work(2, "work2");
		recording.setWorks(asSet(work1, work2));
		
		// release
		release = release();
		
		// release contributors
		releasePerformer = createContributor(releaseArtist, PERFORMER);
		releaseContributors = asSet(releasePerformer);
		release.setContributors(releaseContributors);
		
		// recording contributors
		recordingProducer = createContributor(recordingArtist, PRODUCER);
		recordingContributors = asSet(recordingProducer);
		recording.setContributors(recordingContributors);

		// work contributors
		workComposer1 = createContributor(workArtist1, COMPOSER);
		workContributors1 = asSet(workComposer1);
		work1.setContributors(workContributors1);
		
		workAuthor2 = createContributor(workArtist2, LYRICS);
		workContributors2 = asSet(workAuthor2);
		work2.setContributors(workContributors2);

		// The Track To Test
		track = track(1, release, recording);
	}
	
	private ObservableTrack track(int trackNumber, ObservableRelease release, ObservableRecording recording) {
		ObservableTrack t = new ObservableTrack();
		t.setId(String.valueOf(trackNumber));
		t.setNumber(Integer.valueOf(trackNumber));
		t.setRelease(release);
		t.setRecording(recording);
		t.postInflate(); // hook listeners
		
		List<Track> moreTracks = new ArrayList(release.getTracks());
		moreTracks.add(t);
		release.setTracks(moreTracks );
		return t;
	}

	private ObservableRecording recording(int id, String name) {
		ObservableRecording r = new ObservableRecording();
		r.setId(String.valueOf(id));
		r.setName(name);
		r.markInflated();
		return r;
	}

	private ObservableWork work(int id, String name) {
		ObservableWork w = new ObservableWork();
		w.setId(String.valueOf(id));
		w.setName(name);
		return w;
	}

	private ObservableRelease release() {
		ObservableRelease r = new ObservableRelease();
		return r;
	}

	private ObservableArtist artist(int id, String name) {
		ObservableArtist a = new ObservableArtist();
		a.setId(String.valueOf(id));
		a.setName(name);
		return a;
	}
	
	private Set asSet(Object... elements) {
		return new HashSet(Arrays.asList(elements));
	}

	private ObservableContributor createContributor(ObservableArtist a, String role) {
		ObservableContributor c = new ObservableContributor();
		c.setId(a.getId());
		c.setArtist(a);
		c.setType(role);
		return c;
	}

	@Test
	public void testSetup() throws Exception {
		assertTrue(release.getContributors().contains(releasePerformer));
		assertTrue(release.getTracks().contains(track));
		assertTrue(track.getRecording()==recording);
		assertTrue(recording.getWorks().containsAll(asSet(work1, work2)));
	}
	
	@Test
	public void testTitle() throws Exception {
		assertEquals(recording.getName(), track.getTitle());
		
		track.addPropertyChangeListener(ObservableTrack.PROP_title, listener);
		recording.setName("Kalle");
		
		assertEquals("Kalle", track.getTitle());
		assertTrue(listener.isChanged());
	}
	
	@Test
	public void testEffectiveContributors() throws Exception {
		Set<Contributor> expected = join(release, recording, work1, work2);
		int expectedSize = expected.size();
		assertEquals(expectedSize, track.getContributors().size());
//		assertEquals(expected, track.getContributors());  // Does not work, we get contributors with origin, not plain contributors
		
		add(release, releaseArtist, CONDUCTOR);
		assertEquals("Bad size after add", expectedSize+1, track.getContributors().size());
		
		remove(release, releasePerformer);
		assertEquals("Bad size after add", expectedSize, track.getContributors().size());
		
		clear(release, recording, work1, work2);
		assertTrue("Not empty", track.getContributors().isEmpty());
	}

	@Test
	public void testEffectiveContributorsChange() throws Exception {
		track.addPropertyChangeListener(AbstractContributableEntity.PROP_contributors, listener);
		
		remove(recording, recordingProducer);
		assertTrue("Not changed", listener.isChanged());

		remove(release, releasePerformer);
		assertTrue("Not changed", listener.isChanged());

		remove(work1, workComposer1);
		assertTrue("Not changed", listener.isChanged());

		remove(work2, workAuthor2);
		assertTrue("Not changed", listener.isChanged());

		add(recording, recordingProducer);
		assertTrue("Not changed", listener.isChanged());

		add(release, releasePerformer);
		assertTrue("Not changed", listener.isChanged());

		add(work1, workComposer1);
		assertTrue("Not changed", listener.isChanged());

		add(work2, workAuthor2);
		assertTrue("Not changed", listener.isChanged());
	}
	
	@Test
	public void testEffectiveContributorsChangeName() throws Exception {
		track.addPropertyChangeListener(AbstractContributableEntity.PROP_contributors, listener);
		
		recordingProducer.getArtist().setName("other");
		assertTrue("Not changed", listener.isChanged());

		releasePerformer.getArtist().setName("other");
		assertTrue("Not changed", listener.isChanged());

		workComposer1.getArtist().setName("other");
		assertTrue("Not changed", listener.isChanged());

		workAuthor2.getArtist().setName("other");
		assertTrue("Not changed", listener.isChanged());

	}
	
	@Test
	public void testEffectiveContributorsChangeType() throws Exception {
		track.addPropertyChangeListener(AbstractContributableEntity.PROP_contributors, listener);
		
		recordingProducer.setType(PERFORMER);
		assertTrue("Not changed", listener.isChanged());

		releasePerformer.setType(PRODUCER);
		assertTrue("Not changed", listener.isChanged());

		workComposer1.setType(LYRICS);
		assertTrue("Not changed", listener.isChanged());

		workAuthor2.setType(COMPOSER);
		assertTrue("Not changed", listener.isChanged());

	}

	@Test
	public void testEffectiveContributorsChangeArtist() throws Exception {
		track.addPropertyChangeListener(AbstractContributableEntity.PROP_contributors, listener);
		
		recordingProducer.setArtist(releaseArtist);
		assertTrue("Not changed", listener.isChanged());

		releasePerformer.setArtist(recordingArtist);
		assertTrue("Not changed", listener.isChanged());

		workComposer1.setArtist(recordingArtist);
		assertTrue("Not changed", listener.isChanged());

		workAuthor2.setArtist(recordingArtist);
		assertTrue("Not changed", listener.isChanged());

	}
	
	
	private void add(AbstractContributableEntity e, ObservableArtist a, String role) {
		add(e, createContributor(a, role));
	}

	private void add(AbstractContributableEntity e, ObservableContributor c) {
		assert !e.getContributors().contains(c) : "Bad test setup; attempting to add existing member: "+c+" to " + e + ": " + e.getContributors();
		Set<Contributor> set = new HashSet(e.getContributors());
		set.add(c);
		e.setContributors(set);
	}

	private void remove(AbstractContributableEntity e, Contributor c) {
		assert e.getContributors().contains(c) : "Bad test setup; attempting to remove a non-member: "+c+" from " + e + ": " + e.getContributors();
		Set<Contributor> set = new HashSet(e.getContributors());
		set.remove(c);
		e.setContributors(set);
	}

	private Set<Contributor> join(AbstractContributableEntity... entities) {
		Set<Contributor> set = new HashSet();
		for (AbstractContributableEntity e : entities) {
			set.addAll(e.getContributors());
		}
		return set;
	}

	private void clear(AbstractContributableEntity... entities) {
		for (AbstractContributableEntity e : entities) {
			e.setContributors(Collections.EMPTY_SET);
		}
	}

}
