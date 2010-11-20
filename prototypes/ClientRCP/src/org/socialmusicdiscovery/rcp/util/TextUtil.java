package org.socialmusicdiscovery.rcp.util;

public class TextUtil {

	private TextUtil() {}

	public static String getShortText(String text) {
		if (text.length()>20) {
			String head = text.substring(0,9);
			String tail = text.substring(11,20);
			return head+"..."+tail;
		}
		return text;
	}

}
