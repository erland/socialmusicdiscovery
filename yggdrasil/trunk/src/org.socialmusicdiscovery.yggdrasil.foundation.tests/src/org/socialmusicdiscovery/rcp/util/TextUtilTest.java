package org.socialmusicdiscovery.rcp.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class TextUtilTest {
	@Test
	public void testGetText() {
		String result = TextUtil.getText(String.class);
		assertEquals("String", result);
	}
	
	@Test
	public void testGetShortText() {
		String original = "A really long text which will be very hard to fit in the user interface";
		String result = TextUtil.getShortText(original);
		assertTrue("Not shorter", result.length()<original.length());
		assertTrue("No dots", result.contains("...")); // won't work if/when we localize strings 
		assertTrue("Bad start: "+result, result.startsWith(original.substring(0,5)));
		assertTrue("Bad end: "+result, result.endsWith(original.substring(original.length()-5)));
	}

	@Test
	public void testToInitialUppercase() {
		String[] patterns = {
				"name",	"Name",
				"s",	"S",
				"",		"",
				"alv",	"Alv",
//				"älv",	"Älv", // fails when run from commandline build on WinXP?
		};
		for (int i = 0; i < patterns.length;) {
			String in = patterns[i++];
			String expected = patterns[i++];
			assertEquals("Input: " + in, expected, TextUtil.toInitialUppercase(in));
		}
	}
}
