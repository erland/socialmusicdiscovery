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

package org.socialmusicdiscovery.yggdrasil.foundation.util;

import static org.socialmusicdiscovery.yggdrasil.foundation.test.AnObservable.CHILD;
import static org.socialmusicdiscovery.yggdrasil.foundation.test.AnObservable.CHILDREN;
import static org.socialmusicdiscovery.yggdrasil.foundation.test.AnObservable.NAME;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.socialmusicdiscovery.yggdrasil.foundation.content.AbstractTestCase;
import org.socialmusicdiscovery.yggdrasil.foundation.test.AnObservable;
import org.socialmusicdiscovery.yggdrasil.foundation.util.ChangeMonitor;
import org.socialmusicdiscovery.yggdrasil.foundation.util.GenericWritableSet;
import org.socialmusicdiscovery.yggdrasil.foundation.util.ChangeMonitor.PropertyData;

/**
 * @author Peer Törngren
 *
 */
public class ChangeMonitorTest extends AbstractTestCase {
	public interface MyInterface {
		Set getCollection();
	}

	public class MySuperClass {
		public GenericWritableSet<String> getCollection() {
			return new GenericWritableSet<String>();
		}
	}

	public class MyClass extends MySuperClass implements MyInterface {
		@Override // MUST OVERRIDE TO DECLARE GENERIC RETURN TYPE   
		public GenericWritableSet<String> getCollection() {
			return super.getCollection();
		}
	}

	private AnObservable root;
	private MyObserver observer;
	private AnObservable rootChild;
	private AnObservable grandChild;
	private AnObservable grandGrandChild;

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
		rootChild = new AnObservable("childOfRoot");
		grandChild = new AnObservable("grandChildOfRoot");
		grandGrandChild = new AnObservable("grandGrandChildOfRoot");
		
		root.setChild(rootChild);
		root.add(rootChild);
		
		rootChild.setChild(grandChild);
		rootChild.add(grandChild);
		
		grandChild.setChild(grandGrandChild);
		grandChild.add(grandGrandChild);
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
		rootChild.changeName();
		assertChanged();
		
		ChangeMonitor.observe(observer, root, CHILD, CHILD, NAME);
		grandChild.changeName();
		assertChanged();
	}

	@Test
	public void testObjectChange() {
		ChangeMonitor.observe(observer, root, CHILD);
		root.setChild(grandChild);
		assertChanged();
		root.setChild(rootChild);
		assertChanged();
		root.setChild(null);
		assertChanged();
		root.setChild(rootChild);
		assertChanged();
	}

	@Test
	public void testNestedObjectChange() {
		ChangeMonitor.observe(observer, root, CHILD, CHILD);
		rootChild.setChild(null);
		assertChanged();
		rootChild.setChild(rootChild);
		assertChanged();
		rootChild.setChild(grandChild);
		assertChanged();

		ChangeMonitor.observe(observer, root, CHILD, CHILD, CHILD);
		grandChild.setChild(rootChild);
		assertChanged();
		rootChild.setChild(rootChild);
		assertChanged();
	}
	
	@Test
	public void testNestedObjectAndNameChange() {
		ChangeMonitor.observe(observer, root, CHILD, CHILD, NAME);
		rootChild.setChild(null);
		assertChanged();
		rootChild.setChild(rootChild);
		assertChanged();
		rootChild.setChild(grandChild);
		assertChanged();
		
		grandChild.changeName();
		assertChanged();
		
		rootChild.changeName();
		assertUnchanged();
	}
	
	@Test
	public void testAnyChange() {
		ChangeMonitor.observe(observer, root, (String) null);

		root.changeName();
		assertChanged();
		
		rootChild.changeName();
		assertUnchanged();
	}
	
	@Test
	public void testNestedObjectAndAnyChange() {
		ChangeMonitor.observe(observer, root, CHILD, CHILD, null);

		grandChild.changeName();
		assertChanged();
		
		rootChild.changeName();
		assertUnchanged();
	}

	@Test
	public void testCollectionAndAnyChange() {
		ChangeMonitor.observe(observer, root, CHILDREN, null);

		rootChild.changeName();
		assertChanged();
		
		grandChild.changeName();
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
	
	@Test
	public void testGetGenericWritableSet() {
		Class<MyClass> beanClass = MyClass.class;
		PropertyDescriptor descriptor = ChangeMonitor.getDescriptor(beanClass, "collection");
		Class elementType = ChangeMonitor.resolveElementType(beanClass, descriptor);
		assertEquals(String.class, elementType);
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
		root.remove(rootChild);
		assertChanged();
		root.add(rootChild);
		assertChanged();
		
		ChangeMonitor.observe(observer, root, CHILD, CHILDREN);
		rootChild.remove(grandChild);
		assertChanged();
		rootChild.add(grandChild);
		assertChanged();

		ChangeMonitor.observe(observer, root, CHILD, CHILD, CHILDREN);
		grandChild.remove(grandGrandChild);
		assertChanged();
		grandChild.add(grandGrandChild);
		assertChanged();
	}

	@Test
	public void testPropertyInCollection() {
		ChangeMonitor.observe(observer, root, CHILDREN, NAME);
		
		rootChild.changeName();
		assertChanged();
	}

	@Test
	public void testPropertyInCollectionAfterAdd() {
		ChangeMonitor.observe(observer, root, CHILDREN, NAME);
		root.remove(rootChild);
		assertChanged();
		root.add(rootChild);
		assertChanged();
//		assertListener(child1, NAME);

		rootChild.changeName();
		assertChanged();
		
		ChangeMonitor.observe(observer, root, CHILD, CHILDREN, NAME);
		rootChild.remove(grandChild);
		assertChanged();
		rootChild.add(grandChild);
		assertChanged();
		grandChild.changeName();
		assertChanged();

		ChangeMonitor.observe(observer, root, CHILD, CHILD, CHILDREN, NAME);
		grandChild.remove(grandGrandChild);
		assertChanged();
		grandChild.add(grandGrandChild);
		assertChanged();
		grandGrandChild.changeName();
		assertChanged();
		grandGrandChild.changeName();
		assertChanged();
	}

	@Test
	public void testCollectionInCollection() {
		ChangeMonitor.observe(observer, root, CHILDREN, CHILDREN, CHILDREN);
		grandChild.remove(grandGrandChild);
		assertChanged();
		grandChild.add(grandGrandChild);
		assertChanged();
	}
	
	@Test
	public void testCollectionInCollectionAfterAdd() {
		ChangeMonitor.observe(observer, root, CHILDREN, CHILDREN, CHILDREN);
		root.remove(rootChild);
		assertChanged();
		root.add(rootChild);
		assertChanged();
		rootChild.remove(grandChild);
		assertChanged();
		rootChild.add(grandChild);
		assertChanged();
		grandChild.remove(grandGrandChild);
		assertChanged();
		grandChild.add(grandGrandChild);
		assertChanged();
	}
	
	@Test
	public void testPropertyInCollectionInCollection() {
		ChangeMonitor.observe(observer, root, CHILDREN, CHILDREN, NAME);
		
		grandChild.changeName();
		assertChanged();
		
		rootChild.changeName();
		assertUnchanged();
		root.changeName();
		assertUnchanged();
	}

	@Test
	public void testPopertyInCollectionInCollectionAfterAdd() {
		ChangeMonitor.observe(observer, root, CHILDREN, CHILDREN, NAME);
		rootChild.remove(grandChild);
		assertChanged();
		rootChild.add(grandChild);
		assertChanged();
		
		grandChild.changeName();
		assertChanged();
	}

	private void assertUnchanged() {
		assertFalse("Unexpected change detected"+dump(), observer.isChanged());
	}

	private void assertChanged() {
		assertTrue("No change detected"+dump(), observer.isChanged());
	}


	private String dump() {
		return dump(root, rootChild, grandChild);
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