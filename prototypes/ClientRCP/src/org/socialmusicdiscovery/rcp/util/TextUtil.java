package org.socialmusicdiscovery.rcp.util;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

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

	/**
	 * Get a human readable text for supplied class.
	 * Class is expected to be a subtype of {@link SMDIdentity}.
	 * @param type
	 * @return String
	 */
	public static String getText(Class type) {
		// TODO externalize, now using raw interface as name
		return type.getSimpleName();
	}

}
