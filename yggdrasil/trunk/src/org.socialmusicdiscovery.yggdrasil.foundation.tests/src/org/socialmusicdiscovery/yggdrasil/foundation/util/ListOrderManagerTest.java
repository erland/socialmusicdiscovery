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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.socialmusicdiscovery.yggdrasil.foundation.content.AbstractObservableEntity;
import org.socialmusicdiscovery.yggdrasil.foundation.content.AbstractTestCase;
import org.socialmusicdiscovery.yggdrasil.foundation.content.SortAsComparator;
import org.socialmusicdiscovery.yggdrasil.foundation.util.ListOrderManager;

/**
 * @author Peer Törngren
 *
 */
public class ListOrderManagerTest extends AbstractTestCase {

	private class MyEntity extends AbstractObservableEntity {
		public MyEntity(int i) {
			setName("Entity#"+i);
		}

		@Override
		public String toString() {
			return "'"+getName()+"/"+getSortAs()+"'";
		}
		
	}

	private WritableList list;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		list = new WritableList();
		for (int i = 0; i < 12; i++) {
			list.add(new MyEntity(i));
		}
	}
	
	@After
	public void tearDown() throws Exception {
		list.dispose();
		super.tearDown();
	}

	/**
	 * Test method for {@link org.socialmusicdiscovery.yggdrasil.foundation.util.ListOrderManager#manage(org.eclipse.core.databinding.observable.list.IObservableList)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testManage() {
		ListOrderManager.manage(list);
		MyEntity e0 = (MyEntity) list.get(0);
		MyEntity a = (MyEntity) list.get(4);
		MyEntity b = new MyEntity(6);
		MyEntity c = (MyEntity) list.get(5);
		
		list.add(5, b);
		List sortableClone = new ArrayList(list);
		
		assertTrue("Not properly rewritten, maybe listener did not get event? "+e0, e0.getSortAs().startsWith("001"));
		
		assertOrder(sortableClone, a, 4);
		assertOrder(sortableClone, b, 5);
		assertOrder(sortableClone, c, 6);
		
		Collections.sort(sortableClone, SortAsComparator.instance());
		
		assertOrder(sortableClone, a, 4);
		assertOrder(sortableClone, b, 5);
		assertOrder(sortableClone, c, 6);
		
	}

	private void assertOrder(List list, MyEntity entity, int expectedOrder) {
		assertEquals(entity+", list="+list, expectedOrder, list.indexOf(entity));
	}

}
