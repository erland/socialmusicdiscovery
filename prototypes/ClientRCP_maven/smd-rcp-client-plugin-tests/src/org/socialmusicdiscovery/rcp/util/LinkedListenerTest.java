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

import static org.socialmusicdiscovery.rcp.test.AnObservable.CHILD;
import static org.socialmusicdiscovery.rcp.test.AnObservable.CHILDREN;
import static org.socialmusicdiscovery.rcp.test.AnObservable.NAME;

import java.util.Set;

import org.junit.Test;
import org.socialmusicdiscovery.rcp.content.AbstractTestCase;
import org.socialmusicdiscovery.rcp.test.AnObservable;
import org.socialmusicdiscovery.rcp.util.ChangeMonitor.PropertyData;

/**
 * @author Peer Törngren
 *
 */
public class LinkedListenerTest extends AbstractTestCase {

	private PropertyData children;
	private PropertyData name;
	private PropertyData child;

	private AnObservable root;
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// no-op, we just need something to feed to the constructor
		}
	};
	private AnObservable child1;
	private AnObservable child2;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		root = new AnObservable("root");
		child1 = new AnObservable("1");
		child2 = new AnObservable("2");
		
		child = createData(false);
		child.propertyName = CHILD;
		child.propertyType = AnObservable.class;
		
		children = createData(true);
		children.propertyName = CHILDREN;
		children.propertyType = Set.class;
		children.elementType = AnObservable.class;
		
		name = createData(true);
		name.propertyName = NAME;
		name.propertyType = String.class;
	}

	private PropertyData createData(boolean isCollection) {
		PropertyData d = new PropertyData(AnObservable.class);
		d.isCollection = isCollection;
		d.elementType = isCollection ? AnObservable.class : null;
		return d;
	}

	@Test
	public void testBasic() {
		LinkedListener testee = testee(name);
		assertTrue(testee.getChain().isEmpty());

		testee = testee(child);
		assertTrue(testee.getChain().isEmpty());

		testee = testee(children);
		assertTrue(testee.getChain().isEmpty());
	}

	@Test
	public void testSimpleChain() {
		LinkedListener testee = testee(child, name);
		assertEquals(0, testee.getChain().size());
		root.setChild(child1);
		assertEquals(1, testee.getChain().size());
		root.setChild(null);
		assertEquals(0, testee.getChain().size());
		root.setChild(child1);
		assertEquals(1, testee.getChain().size());
	}			
	@Test
	public void testSimpleChainWithCollection() {
		LinkedListener testee = testee(children, name);
		assertEquals(0, testee.getChain().size());
		root.add(child1);
		assertEquals(1, testee.getChain().size());
		root.remove(child1);
		assertEquals(0, testee.getChain().size());
		root.add(child1);
		assertEquals(1, testee.getChain().size());
	}
	
	@Test
	public void testChainWithTwoInstances() {
		LinkedListener testee = testee(child, child, name);
		assertEquals(0, testee.getChain().size());
		root.setChild(child1);
		assertEquals(1, testee.getChain().size());
		child1.setChild(child2);
		assertEquals(2, testee.getChain().size());
		child1.setChild(null);
		assertEquals(1, testee.getChain().size());
		child1.setChild(child2);
		assertEquals(2, testee.getChain().size());
		
		root.setChild(null);
		assertEquals(0, testee.getChain().size());
		root.setChild(child1);
		assertEquals(2, testee.getChain().size());
	}

	@Test
	public void testChainWithTwoCollections() {
		LinkedListener testee = testee(children, children, name);
		assertEquals(0, testee.getChain().size());
		root.add(child1);
		assertEquals(1, testee.getChain().size());
		child1.add(child2);
		assertEquals(2, testee.getChain().size());
		child1.remove(child2);
		assertEquals(1, testee.getChain().size());
	}

	@Test
	public void testChainWithTwoCollectionsAndDeleteIntermediate() {
		LinkedListener testee = testee(children, children, name);
		assertEquals(0, testee.getChain().size());
		root.add(child1);
		assertEquals(1, testee.getChain().size());
		child1.add(child2);
		assertEquals(2, testee.getChain().size());
		
		root.remove(child1);
		assertEquals(0, testee.getChain().size());
		root.add(child1);
		assertEquals(2, testee.getChain().size());
	}
	
	/**
	 * Make sure we can handle property change events with <code>null</code>
	 * values.
	 */
	@Test
	public void testPropertyRefreshEvent() {
		testee(children, name);
		root.add(child1);
		child1.firePropertyChangeEventWithoutValues(name.propertyName);
	}
	
	private LinkedListener testee(PropertyData... data) {
		return new LinkedListener(root, runnable, data);
	}			
}