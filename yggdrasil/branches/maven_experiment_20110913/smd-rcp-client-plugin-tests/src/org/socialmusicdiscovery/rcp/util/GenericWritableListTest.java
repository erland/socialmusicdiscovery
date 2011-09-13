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

package org.socialmusicdiscovery.rcp.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.socialmusicdiscovery.rcp.content.AbstractTestCase;

/**
 * @author Peer Törngren
 *
 */
public class GenericWritableListTest extends AbstractTestCase {

	private GenericWritableList<String> list;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		list = new GenericWritableList<String>();
	}

	@Test
	public void testAddRemove() {
		list.addListChangeListener(listener);
		list.add("A");
		assertTrue(list.contains("A"));
		assertTrue(listener.isChanged());
		
		list.remove("A");
		assertFalse(list.contains("A"));
		assertTrue(list.isEmpty());
		assertTrue(listener.isChanged());

	}

	/**
	 * This is not really a test, but rather a verification that the code
	 * compiles, and an example on how to loop over the set.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testIterate1() {
		list.addAll(Arrays.asList("A", "B"));
		String actual = "";
		for (String s : (List<String>)list) {
			actual+=s;
		}
		assertEquals("Bad loop", "AB", actual);
	}

	/**
	 * This is not really a test, but rather a verification that the code
	 * compiles, and an example on how to loop over the set.
	 */
	@Test
	public void testIterate2() {
		list.addAll(Arrays.asList("A", "B"));
		Iterator<String> iter = list.iterator();
		String actual = "";
		while(iter.hasNext()) {
			actual+=iter.next();
		}
		assertEquals("Bad loop", "AB", actual);
	}

			
}