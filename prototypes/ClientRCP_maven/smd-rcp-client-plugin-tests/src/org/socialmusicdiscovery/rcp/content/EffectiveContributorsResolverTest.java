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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.socialmusicdiscovery.server.business.model.core.Contributor;

/**
 * @author Peer Törngren
 */
@SuppressWarnings("unchecked")
public class EffectiveContributorsResolverTest {

	private static final String COMPOSER = "composer";
	private static final String CONDUCTOR = "conductor";
	private static final String PERFORMER = "performer";
	
	private static final List EMPTY = Collections.EMPTY_LIST; 
	
	private List<Contributor> conductors1;
	private List<Contributor> composers1;
	private List<Contributor> performers1;
	private List<Contributor> composers2;
	private List<Contributor> conductors2;
	private List<Contributor> performers2;
	private List<Contributor> conductors3;
	private List<Contributor> composers3;
	private List<Contributor> performers3;
	
	private Contributor performerA;
	private Contributor performerB;
	private Contributor performerC;
	private Contributor performerD;
	private Contributor conductorA;
	private Contributor conductorB;
	private Contributor conductorC;
	private Contributor conductorD;
	private Contributor composerA;
	private Contributor composerB;
	private Contributor composerC;
	private Contributor composerD;

	private Set actual;
	private int guid;


	@Before
	public void setup() throws Exception {
		guid = 0;
		conductors1 = fake(CONDUCTOR, 1);
		composers1 = fake(COMPOSER, 1);
		performers1 = fake(PERFORMER, 1);
		
		conductors2 = fake(CONDUCTOR, 2);
		composers2 = fake(COMPOSER, 2);
		performers2 = fake(PERFORMER, 2);

		conductors3 = fake(CONDUCTOR, 3);
		composers3 = fake(COMPOSER, 3);
		performers3 = fake(PERFORMER, 3);
		
		List<Contributor> performers = fake(PERFORMER, 4);
		performerA = performers.get(0);
		performerB = performers.get(1);
		performerC = performers.get(2);
		performerD = performers.get(3);
		List<Contributor> conductors = fake(CONDUCTOR, 4);
		conductorA = conductors.get(0);
		conductorB = conductors.get(1);
		conductorC = conductors.get(2);
		conductorD = conductors.get(3);
		List<Contributor> composers = fake(COMPOSER, 4);
		composerA = composers.get(0);
		composerB = composers.get(1);
		composerC = composers.get(2);
		composerD = composers.get(3);
	}

	@After
	public void tearDown() throws Exception {
		guid = 0;
	}
	
	@Test
	public void testBasic() throws Exception {
		test(conductors1, conductors2);
		expect(conductors1);
		reject(conductors2);

		Collection join1 = join(conductors1, composers1);
		test(join1, conductors2);
		expect(join1);
		reject(conductors2);

		test(conductors1, join(conductors2, composers2));
		expect(conductors1, composers2);
		reject(conductors2);

		test(EMPTY, performers2, composers3);
		expect(performers2, composers3);
		reject(EMPTY);
	}

	@Test
	public void testExtended() throws Exception {

		test(performers1, join(performers2, conductors2), join(performers3, composers3, conductors3));
		expect(performers1, conductors2, composers3);
		reject(performers2, performers3, conductors3);

		Collection j1 = join(composers1, conductors1, performers1);
		Collection j2 = join(composers2, conductors2, performers2);
		Collection j3 = join(composers3, conductors3, performers3);
		test(j1, j2, j3);
		expect(j1);
		reject(j2, j3);
	}

	@Test
	public void testABCD() throws Exception {
		test(join(performerA), join(performerB, performerC), join(performerD));
		expect(performerA);
		reject(performerB, performerC, performerD);
		
		test(EMPTY, join(conductorB));
		expect(conductorB);
		reject(EMPTY);

		test(EMPTY, join(performerB, conductorB));
		expect(performerB, conductorB);
		reject(EMPTY);

		test(join(performerA), join(performerB, conductorB), join(conductorC), join(performerD, composerD));
		expect(performerA, conductorB, composerD);
		reject(performerB, conductorC, performerD);

		test(EMPTY, join(performerB, conductorB), join(conductorC), join(performerD, composerD));
		expect(performerB, conductorB, composerD);
		reject(conductorC, performerD);

		test(EMPTY, EMPTY, join(conductorC), join(performerD, composerD));
		expect(conductorC, performerD, composerD);
		reject(EMPTY);
		
		Collection a = join(performerA, conductorA, composerA);
		Collection b = join(performerB, conductorB, composerB);
		Collection c = join(performerC, conductorC, composerC);
		Collection d = join(performerD, conductorD, composerD);
		test(a, b, c, d);
		expect(a);
		reject(b, c, d);
	}
	
	private void test(Collection... collections) {
		actual = new EffectiveContributorsResolver(collections).getEffectiveContributors();
	}
	
	private void expect(Contributor... expected) {
		expect(join(expected));
	}
	
	private void expect(Collection... expected) {
		Collection allExpected = join(expected);
		assertTrue(missing(allExpected, actual), actual.containsAll(allExpected));
		assertEquals(extra(allExpected, actual), allExpected.size(), actual.size());
	}

	private void reject(Contributor... unexpected) {
		reject(join(unexpected));
	}
	
	private void reject(Collection... unexpected) {
		Collection allUnexpected = join(unexpected);
		Collection shouldBeEmpty = new ArrayList(actual);
		shouldBeEmpty.retainAll(allUnexpected);
		assertTrue(extra(shouldBeEmpty, actual), shouldBeEmpty.isEmpty());
	}

	private List<Contributor> fake(String type, int size) throws Exception {
		List<Contributor> result = new ArrayList<Contributor>();
		for (int i = 0; i < size; i++) {
			ObservableContributor fake = new ObservableContributor();
			String id = String.valueOf(guid++);
			fake.setId(id);
			String name = fake.getClass().getSimpleName()+"#"+i;
			fake.setName(name);
			fake.setType(type);
			result.add(fake);
		}
		return result;
	}

	private static Collection join(Collection... collections) {
		Collection result = new HashSet();
		for (Collection c : collections) {
			result.addAll(c);
		}
		return result;
	}


	private static Collection join(Contributor... performers) {
		return Arrays.asList(performers);
	}

	private static String missing(Collection expected, Collection actual) {
		Collection missing = new ArrayList(expected);
		missing.removeAll(actual);
		String msg = "Missing "+missing.size()+" of "+expected.size()+":";
		for (Object m : missing) {
			msg += "\n\t" + m;
		}
		return msg;
	}

	private static String extra(Collection expected, Collection actual) {
		Collection extra = new ArrayList(actual);
		extra.removeAll(expected);
		int nofExtra = actual.size()-expected.size();
		String msg = "Unexpected:  "+nofExtra+" of "+actual.size()+":";
		for (Object m : extra) {
			msg += "\n\t" + m;
		}
		return msg;
	}

}
