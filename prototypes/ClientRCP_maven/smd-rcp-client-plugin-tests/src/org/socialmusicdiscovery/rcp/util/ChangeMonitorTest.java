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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.socialmusicdiscovery.rcp.test.AbstractTestCase;
import org.socialmusicdiscovery.rcp.test.AnObservable;
import org.socialmusicdiscovery.rcp.util.ChangeMonitor.PropertyData;

/**
 * @author Peer Törngren
 *
 */
public class ChangeMonitorTest extends AbstractTestCase {
	private AnObservable root;
	private MyObserver observer;
	private AnObservable child1;
	private AnObservable child2;

	private class MyObserver implements Runnable {

		private boolean isChanged = false;

		@Override
		public void run() {
			this.isChanged  = true;
		}

		public boolean isChanged() {
			boolean result = this.isChanged;
			this.isChanged = false;
			return result;
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		observer = new MyObserver();
		
		root = new AnObservable("root");
		child1 = new AnObservable("child1");
		child2 = new AnObservable("child2");
		
		root.setChild(child1);
		root.add(child1);
		
		child1.setChild(child2);
		child1.add(child2);
		
		child2.setChild(root);
		child2.add(root);
	}

	@Test
	public void testSetupAndBasicPropertyChange() {
		ChangeMonitor.observe(observer, root, NAME);
		assertFalse("Bad initial state", observer.isChanged());
		root.changeName();
		assertChanged();
		assertFalse("Unexpected change detected - bad test case?", observer.isChanged());
	}

	@Test
	public void testNestedProperty() {
		ChangeMonitor.observe(observer, root, CHILD, NAME);
		child1.changeName();
		assertChanged();
		
		ChangeMonitor.observe(observer, root, CHILD, CHILD, NAME);
		child2.changeName();
		assertChanged();
	}

	@Test
	public void testObjectChange() {
		ChangeMonitor.observe(observer, root, CHILD);
		root.setChild(child2);
		assertChanged();
		root.setChild(child1);
		assertChanged();
		root.setChild(null);
		assertChanged();
		root.setChild(child1);
		assertChanged();
	}

	@Test
	public void testNestedObjectChange() {
		ChangeMonitor.observe(observer, root, CHILD, CHILD);
		child1.setChild(null);
		assertChanged();
		child1.setChild(child1);
		assertChanged();
		child1.setChild(child2);
		assertChanged();

		ChangeMonitor.observe(observer, root, CHILD, CHILD, CHILD);
		child2.setChild(child1);
		assertChanged();
		child1.setChild(child1);
		assertChanged();
	}
	
	@Test
	public void testNestedObjectAnNameChange() {
		ChangeMonitor.observe(observer, root, CHILD, CHILD, NAME);
		child1.setChild(null);
		assertChanged();
		child1.setChild(child1);
		assertChanged();
		child1.setChild(child2);
		assertChanged();

		ChangeMonitor.observe(observer, root, CHILD, CHILD, CHILD);
		child2.setChild(child1);
		assertChanged();
		child1.setChild(child1);
		assertChanged();
		
		child2.changeName();
		assertChanged();
		
		child1.changeName();
		assertUnchanged();
	}
	
	@Test
	public void testPropertyData() {
		List<PropertyData> data = ChangeMonitor.getPropertyData(AnObservable.class, CHILD, CHILDREN, CHILD, CHILDREN, NAME);
		assertData(data, 0, AnObservable.class, null, 				false);
		assertData(data, 1, Set.class, 			AnObservable.class, true);
		assertData(data, 2, AnObservable.class, null, 				false);
		assertData(data, 3, Set.class, 			AnObservable.class, true);
		assertData(data, 4, String.class, 		null, 				false);
	}
	
	private void assertData(List<PropertyData> data, int elementIndex, Class expectedPropertyType, Class expectedElementType, boolean expectedIsCollection) {
		PropertyData d = data.get(elementIndex);
		assertEquals("Bad property type in #"+elementIndex, expectedPropertyType, d.propertyType);
		assertEquals("Bad element type in #"+elementIndex, expectedElementType, d.elementType);
		assertEquals("Bad isCollection  in #"+elementIndex, expectedIsCollection, d.isCollection);
	}

	@Test
	public void testElementInCollection() {
		ChangeMonitor.observe(observer, root, CHILDREN);
		root.remove(child1);
		assertChanged();
		root.add(child1);
		assertChanged();
		
		ChangeMonitor.observe(observer, root, CHILD, CHILDREN);
		child1.remove(child2);
		assertChanged();
		child1.add(child2);
		assertChanged();

		ChangeMonitor.observe(observer, root, CHILD, CHILD, CHILDREN);
		child2.remove(root);
		assertChanged();
		child2.add(root);
		assertChanged();
	}

	@Test
	public void testPropertyInCollection() {
		ChangeMonitor.observe(observer, root, CHILDREN, NAME);
		
		child1.changeName();
		assertChanged();
	}

	@Test
	public void testPropertyInCollectionAfterAdd() {
		ChangeMonitor.observe(observer, root, CHILDREN, NAME);
		root.remove(child1);
		assertChanged();
		root.add(child1);
		assertChanged();
//		assertListener(child1, NAME);

		child1.changeName();
		assertChanged();
		
		ChangeMonitor.observe(observer, root, CHILD, CHILDREN, NAME);
		child1.remove(child2);
		assertChanged();
		child1.add(child2);
		assertChanged();
		child2.changeName();
		assertChanged();

		ChangeMonitor.observe(observer, root, CHILD, CHILD, CHILDREN, NAME);
		child2.remove(root);
		assertChanged();
		child2.add(root);
		assertChanged();
		root.changeName();
		assertChanged();
		root.changeName();
		assertChanged();
	}

	@Test
	public void testCollectionInCollection() {
		ChangeMonitor.observe(observer, root, CHILDREN, CHILDREN, CHILDREN);
		child2.remove(root);
		assertChanged();
		child2.add(root);
		assertChanged();
	}
	
	@Test
	public void testCollectionInCollectionAfterAdd() {
		ChangeMonitor.observe(observer, root, CHILDREN, CHILDREN, CHILDREN);
		root.remove(child1);
		assertChanged();
		root.add(child1);
		assertChanged();
		child1.remove(child2);
		assertChanged();
		child1.add(child2);
		assertChanged();
		child2.remove(root);
		assertChanged();
		child2.add(root);
		assertChanged();
	}
	
	@Test
	public void testPropertyInCollectionInCollection() {
		ChangeMonitor.observe(observer, root, CHILDREN, CHILDREN, NAME);
		
		child2.changeName();
		assertChanged();
		
		child1.changeName();
		assertUnchanged();
		root.changeName();
		assertUnchanged();
	}

	@Test
	public void testPopertyInCollectionInCollectionAfterAdd() {
		ChangeMonitor.observe(observer, root, CHILDREN, CHILDREN, NAME);
		child1.remove(child2);
		assertChanged();
		child1.add(child2);
		assertChanged();
		
		child2.changeName();
		assertChanged();
	}

	private void assertUnchanged() {
		assertFalse("Unexpected change detected"+dump(), observer.isChanged());
	}

	private void assertChanged() {
		assertTrue("No change detected"+dump(), observer.isChanged());
	}


	private String dump() {
		return dump(root, child1, child2);
	}
	
	private static String dump(AnObservable... observables) {
		StringBuilder sb = new StringBuilder();
		for (AnObservable o : observables) {
			sb.append('\n');
			sb.append(o);
			sb.append(", child:");
			sb.append(o.getChild());
			sb.append(", children:");
			sb.append(o.getChildren());
			sb.append(", listeners:");
			sb.append(Arrays.asList(o.getPropertyChangeListeners()));
		}
		return sb.toString();
	}

			
}