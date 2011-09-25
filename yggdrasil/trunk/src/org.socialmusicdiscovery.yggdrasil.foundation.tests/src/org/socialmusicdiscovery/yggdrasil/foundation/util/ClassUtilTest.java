package org.socialmusicdiscovery.yggdrasil.foundation.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.socialmusicdiscovery.yggdrasil.foundation.event.AbstractObservable;
import org.socialmusicdiscovery.yggdrasil.foundation.util.ClassUtil;

import com.google.gson.annotations.Expose;

public class ClassUtilTest {
	
	private class MyClassA {
		
		public String getA() {
			return "a";
		}
		public int getB() {
			return 42;
		}
		public List<String> getC() {
			return Arrays.asList("a", "b");
		}
		
		public Object getReference() {
			return this;
		}
	}

	@SuppressWarnings("unused")
	private class MyClassB extends AbstractObservable {
		private String a;
		@Expose
		private int b;
		@Expose
		private List<String> c = new ArrayList<String>();
		private Object reference;

		public String getA() {
			return a;
		}

		public void setA(String a) {
			firePropertyChange("a", a, this.a = a);
		}

		public int getB() {
			return b;
		}

		public void setB(int b) {
			firePropertyChange("b", b, this.b = b);
		}

		public Collection<String> getC() {
			return c;
		}

		public void setC(List<String> c) {
			updateList("c", this.c, c);
		}

		public Object getReference() {
			return reference;
		}

		public void setReference(Object reference) {
			firePropertyChange("reference", reference, this.reference = reference);
		}

	}

	private MyClassA a;
	private MyClassB b;
	
	@Before
	public void setup() {
		a = new MyClassA();
		b = new MyClassB();
	}
	
	@Test
	public void testCopyPersistentProperties() {
		ClassUtil.copyPersistentProperties(a, b);
		
		assertNull(b.getA());
		assertEquals(a.getB(), b.getB());
		assertEquals(a.getC(), b.getC());
		assertNotSame(a.getC(), b.getC());
		assertNull(b.getReference());
	}
	
	@Test
	public void testCopyProperties() {
		ClassUtil.copyProperties(a, b, "a", "b", "c", "reference");
		
		assertEquals(a.getA(), b.getA());
		assertEquals(a.getB(), b.getB());
		assertEquals(a.getC(), b.getC());
		assertNotSame(a.getC(), b.getC());
		assertEquals(a.getReference(), b.getReference());
		assertSame(a.getReference(), b.getReference());
	}

}
