package org.medee.playground.upnp;

import static org.junit.Assert.*;

import org.cybergarage.net.HostInterface;
import org.junit.Test;

public class HostInterfaceTest {

	@Test
	public void testSetInterface() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetInterface() {
		assertEquals("", HostInterface.getInterface());
	}

	@Test
	public void testGetNHostAddresses() {
		assertTrue(HostInterface.getNHostAddresses() > 0 );
		System.out.println("Number of interfaces: "+ HostInterface.getNHostAddresses());
	}

	@Test
	public void testGetInetAddress() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetHostAddress() {
		for(int i=0; i<HostInterface.getNHostAddresses();i++) {
			System.out.println("Get Host Address: "+HostInterface.getHostAddress(i) );
			System.out.println("    (is IPv4: "+HostInterface.isIPv4Address(HostInterface.getHostAddress(i))+")");
			System.out.println("    (is IPv6: "+HostInterface.isIPv6Address(HostInterface.getHostAddress(i))+")");
		}
	}

	@Test
	public void testIsIPv6Address() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsIPv4Address() {
		fail("Not yet implemented");
	}

	@Test
	public void testHasIPv4Addresses() {
		assertTrue(HostInterface.hasIPv4Addresses());
	}

	@Test
	public void testHasIPv6Addresses() {
		assertTrue(HostInterface.hasIPv6Addresses());
	}

	@Test
	public void testGetIPv4Address() {
		System.out.println(HostInterface.getIPv4Address());
	}

	@Test
	public void testGetIPv6Address() {
		System.out.println(HostInterface.getIPv6Address());
	}

	@Test
	public void testGetHostURL() {
		fail("Not yet implemented");
	}

}
