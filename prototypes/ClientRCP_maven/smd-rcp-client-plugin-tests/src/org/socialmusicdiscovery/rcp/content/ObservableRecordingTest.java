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

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.socialmusicdiscovery.rcp.test.MultiPurposeListener;
import org.socialmusicdiscovery.server.business.model.core.Work;

/**
 * @author Peer Törngren
 */
@SuppressWarnings("unchecked")
public class ObservableRecordingTest extends AbstractTestCase {

	public void setUp() throws Exception {
		super.setUp();
		
		recording = recording(1, null);
		work1 = work(1, "work1");
		work2 = work(2, "work2");
		recording.setWorks(asSet(work1, work2));
		recording.addPropertyChangeListener(ObservableTrack.PROP_name, listener);
		recording.setDirty(false);
		release = release();
		
		track = track(1, release, recording);
	}

	@Test
	public void testSetup() throws Exception {
		assertTrue(recording.getWorks().containsAll(asSet(work1, work2)));
		assertFalse(recording.isDirty());
	}
	
	@Test
	public void testDerivedName() throws Exception {
		assertTrue(contains("work1"));
		assertTrue(contains("work2"));
		assertFalse(recording.isDirty());
		
		work1.setName("Kalle");
		assertTrue(contains("Kalle"));
		assertTrue(listener.isChanged());
		assertFalse(recording.isDirty());
		
		remove(work1);
		assertFalse(contains("Kalle"));
		assertTrue(contains("work2"));
		assertTrue(listener.isChanged());
		assertTrue(recording.isDirty());
		
		work1.setName("Olle");
		assertFalse(contains("Olle"));
		assertFalse(listener.isChanged());
		
		add(work1);
		assertTrue(contains("Olle"));
		assertTrue(contains("work2"));
		assertTrue(listener.isChanged());
		
	}

	public void testName() throws Exception {
		assertTrue(contains("work1"));
		assertTrue(contains("work2"));
		assertFalse(recording.isDirty());
		
		recording.setName("Pelle");
		assertEquals(recording.getName(), "Pelle");
		assertFalse(contains("work1"));
		assertFalse(contains("work2"));
		assertTrue(listener.isChanged());
		assertTrue(recording.isDirty());
	}

	@Test
	public void testDelete() throws Exception {
		assertTrue("Bad setup? Track not found", recording.getTracks().contains(track));
		assertTrue("Bad setup? Track not found", release.getTracks().contains(track));

		MultiPurposeListener recordingListener = listener(recording.getTracks());
		MultiPurposeListener releaseListener = listener(release.getTracks());

		recording.delete();

		assertFalse("Track still present", recording.getTracks().contains(track));
		assertTrue("No event fired", recordingListener.isChanged());

		assertFalse("Track still present", release.getTracks().contains(track));
		assertTrue("No event fired", releaseListener.isChanged());
	}
	
	
	private void remove(ObservableWork w) {
		Set<Work> set = new HashSet(recording.getWorks());
		set.remove(w);
		recording.setWorks(set);
	}

	private void add(ObservableWork w) {
		Set<Work> set = new HashSet(recording.getWorks());
		set.add(w);
		recording.setWorks(set);
	}
	
	protected boolean contains(String probe) {
		String name = recording.getName();
//		System.out.println(name);
		return name.indexOf(probe)>=0;
	}
	
}
