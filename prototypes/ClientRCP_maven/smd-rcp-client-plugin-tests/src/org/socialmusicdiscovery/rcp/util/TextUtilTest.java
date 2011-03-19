package org.socialmusicdiscovery.rcp.util;

import org.junit.Test;

public class TextUtilTest {
	@Test
	public void testGetText() {
		String result = TextUtil.getText(String.class);
		assert result.equals("String");
	}
	
	@Test
	public void testGetShortText() {
		String original = "A really long text which will be very hard to fit in the user interface";
		String result = TextUtil.getShortText(original);
		assert result.length()<original.length();
		assert result.contains("...");
		assert result.startsWith(original.substring(0,10));
		assert result.endsWith(original.substring(original.length()-10));
	}
}
