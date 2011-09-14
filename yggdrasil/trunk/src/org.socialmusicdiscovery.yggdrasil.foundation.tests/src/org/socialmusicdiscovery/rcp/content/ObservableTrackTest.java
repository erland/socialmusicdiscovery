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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.socialmusicdiscovery.rcp.test.MultiPurposeListener;
import org.socialmusicdiscovery.server.business.model.core.Contributor;

/**
 * @author Peer Törngren
 */
@SuppressWarnings("unchecked")
public class ObservableTrackTest extends AbstractTestCase {

	private Set<Contributor> releaseContributors;
	private ObservableArtist releaseArtist;
	private ObservableContributor releasePerformer;
	private ObservableArtist recordingArtist;
	private ObservableArtist workArtist1;
	private ObservableContributor recordingProducer;
	private Set recordingContributors;
	private ObservableContributor workComposer1;
	private Set workContributors1;
	private ObservableContributor workAuthor2;
	private Set workContributors2;
	private ObservableArtist workArtist2;

	public void setUp() throws Exception {
		super.setUp();
		
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
		releasePerformer = contributor(release, releaseArtist, PERFORMER);
		releaseContributors = asSet(releasePerformer);
		release.setContributors(releaseContributors);
		
		// recording contributors
		recordingProducer = contributor(recording, recordingArtist, PRODUCER);
		recordingContributors = asSet(recordingProducer);
		recording.setContributors(recordingContributors);

		// work contributors
		workComposer1 = contributor(work1, workArtist1, COMPOSER);
		workContributors1 = asSet(workComposer1);
		work1.setContributors(workContributors1);
		
		workAuthor2 = contributor(work2, workArtist2, LYRICS);
		workContributors2 = asSet(workAuthor2);
		work2.setContributors(workContributors2);

		// The Track To Test
		track = track(1, release, recording);
		assert !track.isDirty() : "Bad setup, track is dirty";
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
		for (Contributor e : expected) {
			assertTrue("Not effective contributor: "+e, track.isEffectiveContributor(e));
		}
		
		Contributor releaseConductor = add(release, releaseArtist, CONDUCTOR);
		assertEquals("Bad size after add: "+expected, expectedSize+1, track.getContributors().size());
		assertTrue("Not effective contributor: "+releaseConductor, track.isEffectiveContributor(releaseConductor));
		
		assertTrue("Not effective contributor: "+releasePerformer, track.isEffectiveContributor(releasePerformer));
		remove(release, releasePerformer);
		assertEquals("Bad size after add", expectedSize, track.getContributors().size());
		assertFalse("Effective contributor: "+releasePerformer, track.isEffectiveContributor(releasePerformer));
		
		clear(release, recording, work1, work2);
		assertTrue("Not empty", track.getContributors().isEmpty());
		
		assertFalse("Dirty", track.isDirty());
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

		assertFalse("Dirty", track.isDirty());
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

		assertFalse("Dirty", track.isDirty());
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

		assertFalse("Dirty", track.isDirty());
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

		assertFalse("Dirty", track.isDirty());
	}
	
	@Test
	public void testCreate() throws Exception {
		MultiPurposeListener releaseListener = listener(release.getTracks());
		MultiPurposeListener recordingListener = listener(recording.getTracks());

		assertEquals(1, release.getTracks().size());
		assertEquals(1, recording.getTracks().size());
		
		ObservableTrack template = new ObservableTrack();
		template.setRelease(release);
		template.setRecording(recording);

		ObservableTrack testee = new ObservableTrack(template);

		assertTrue(testee.isInflated());
		assertTrue(testee.isDirty());
		assertTrue(recordingListener.isChanged());
		assertTrue(releaseListener.isChanged());
		
		assertEquals(2, release.getTracks().size());
		assertEquals(2, recording.getTracks().size());
		assertTrue(release.getTracks().contains(testee));
		assertTrue(recording.getTracks().contains(testee));
	}

	@Test
	public void testChangeRecording() throws Exception {
		ObservableRecording recording2 = recording();
		recording2.setTracks(new ArrayList<ObservableTrack>());
		
		MultiPurposeListener releaseListener = listener(release.getTracks());
		MultiPurposeListener recordingListener = listener(recording.getTracks());
		MultiPurposeListener recordingListener2 = listener(recording2.getTracks());

		assertEquals(1, release.getTracks().size());
		assertTrue(release.getTracks().contains(track));
		assertEquals(1, recording.getTracks().size());
		assertTrue(recording.getTracks().contains(track));
		
		track.setRecording(recording2);
		
		assertEquals(1, release.getTracks().size());
		assertFalse(releaseListener.isChanged());
		assertTrue(release.getTracks().contains(track));
		
		assertEquals(0, recording.getTracks().size());
		assertTrue(recordingListener.isChanged());
		assertFalse(recording.getTracks().contains(track));

		assertEquals(1, recording2.getTracks().size());
		assertTrue(recordingListener2.isChanged());
		assertTrue(recording2.getTracks().contains(track));
	}
	@Test
	public void testDelete() throws Exception {
		assertTrue("Bad setup? Track not found", release.getTracks().contains(track));

		assertTrue("Bad setup? Track not found", recording.getTracks().contains(track));

		MultiPurposeListener releaseListener = listener(release.getTracks());
		MultiPurposeListener recordingListener = listener(recording.getTracks());

		track.delete();

		assertFalse("Track still present", release.getTracks().contains(track));
		assertTrue("No event fired", releaseListener.isChanged());

		assertFalse("Track still present", recording.getTracks().contains(track));
		assertTrue("No event fired", recordingListener.isChanged());
	}
	
	private ObservableContributor add(AbstractContributableEntity e, ObservableArtist a, String role) {
		return contributor(e, a, role);
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
