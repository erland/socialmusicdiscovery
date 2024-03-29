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

import java.util.Set;

import org.junit.Test;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableArtist;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableContributor;
import org.socialmusicdiscovery.yggdrasil.foundation.test.MultiPurposeListener;

/**
 * @author Peer Törngren
 */
public class ObservableContributorTest extends AbstractTestCase {


	private ObservableContributor contributor;

	public void setUp() throws Exception {
		super.setUp();
		release = release();
		artist = artist();
		contributor = contributor(release, artist, COMPOSER);
	}
	
	@Test
	public void testChangeArtist() throws Exception {
		ObservableArtist artist2 = artist();
		MultiPurposeListener releaseListener = listener(release.getContributors());
		MultiPurposeListener artistListener = listener(artist.getContributions());
		MultiPurposeListener artistListener2 = listener(artist2.getContributions());

		assertEquals(1, release.getContributors().size());
		assertEquals(1, artist.getContributions().size());
		assertEquals(0, artist2.getContributions().size());
		
		contributor.setArtist(artist2);

		assertEquals(1, release.getContributors().size());
		assertTrue(release.getContributors().contains(contributor));
		assertFalse(releaseListener.isChanged());
		
		assertEquals(0, artist.getContributions().size());
		assertFalse(artist.getContributions().contains(contributor));
		assertTrue(artistListener.isChanged());
		
		assertEquals(1, artist2.getContributions().size());
		assertTrue(artist2.getContributions().contains(contributor));
		assertTrue(artistListener2.isChanged());
	}

	@Test
	public void testCreate() throws Exception {
		MultiPurposeListener releaseListener = listener(release.getContributors());
		MultiPurposeListener artistListener = listener(artist.getContributions());

		assertEquals(1, release.getContributors().size());
		assertEquals(1, artist.getContributions().size());
		
		ObservableContributor template = new ObservableContributor();
		template.setOwner(release);
		template.setArtist(artist);
		template.setType("imaginaryNewType");

		ObservableContributor testee = new ObservableContributor(template);

		assertTrue(testee.isInflated());
		assertTrue(testee.isDirty());
		assertTrue(artistListener.isChanged());
		assertTrue(releaseListener.isChanged());
		
		assertEquals(2, release.getContributors().size());
		assertEquals(2, artist.getContributions().size());
		assertTrue(release.getContributors().contains(testee));
		assertTrue(artist.getContributions().contains(testee));
	}
	@Test
	public void testAddDuplicate() throws Exception {
		MultiPurposeListener releaseListener = listener(release.getContributors());
		MultiPurposeListener artistListener = listener(artist.getContributions());

		assertEquals(1, release.getContributors().size());
		assertEquals(1, artist.getContributions().size());
		
		release.getContributors().add(contributor);
		artist.getContributions().add(contributor);

		assertEquals(1, release.getContributors().size());
		assertEquals(1, artist.getContributions().size());
		assertFalse(artistListener.isChanged());
		assertFalse(releaseListener.isChanged());
	}

	@Test
	public void testDelete() throws Exception {
		release.setContributors(asSet(contributor));
		Set<ObservableContributor> releaseContributors = release.getContributors();
		Set<ObservableContributor> artistContributions = artist.getContributions();

		assertTrue(artistContributions.contains(contributor));
		assertTrue(releaseContributors.contains(contributor));
		
		contributor.delete();
		
		assertFalse(artistContributions.contains(contributor));
		assertFalse(releaseContributors.contains(contributor));
	}
	
	@Test
	public void testRemoveFromContributable() throws Exception {
		assertTrue(release.getContributors().contains(contributor));
		
		MultiPurposeListener releaseListener = listener(release.getContributors());

		release.getContributors().remove(contributor);
		
		assertFalse(release.getContributors().contains(contributor));
		assertTrue(releaseListener.isChanged());
	}
	
	@Test
	public void testPersistencyFeatures() throws Exception {
		assertFalse(release.isDirty());
		assertFalse(release.getRemovedDependents().contains(contributor));
		assertFalse(release.getSaveableDependents().contains(contributor));
		assertTrue(release.getDeletableDependents().contains(contributor));
		
		contributor.delete();

		assertFalse(release.isDirty());
		assertFalse(release.getRemovedDependents().contains(contributor));
		assertFalse(release.getSaveableDependents().contains(contributor));
		assertFalse(release.getDeletableDependents().contains(contributor));
		assertEquals(0, release.getDeletableDependents().size());
		assertEquals(0, release.getSaveableDependents().size());
			
		release.getContributors().clear();

		assertFalse(release.isDirty());
		assertFalse(release.getRemovedDependents().contains(contributor));
		assertFalse(release.getSaveableDependents().contains(contributor));
		assertFalse(release.getDeletableDependents().contains(contributor));
	
	}
}
