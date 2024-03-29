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

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

public class TextUtil {

	private TextUtil() {}

	public static String getShortText(String text) {
		if (text.length()>20) {
			String head = text.substring(0,12);
			String tail = text.substring(text.length()-5);
			String result = head+"..."+tail;
			assert result.length()<=20 : "Not shortened: "+result+", size="+result.length();
			return result;
		}
		return text;
	}

	/**
	 * Get a human readable text for supplied class. Class is expected to be a
	 * subtype of {@link SMDIdentity}, but this is not strictly enforced.
	 * 
	 * @param type
	 * @return String
	 */
	public static String getText(Class type) {
		// TODO externalize, now using raw interface as name
		return toInitialUppercase(type.getSimpleName());
	}

	/**
	 * Convert first character to uppercase (e.g. name becomes Name) 
	 * @param string
	 * @return String
	 */
	public static String toInitialUppercase(String string) {
		if (string.length()<1) {
			return string;
		}
		String first = string.substring(0,1).toUpperCase();
		String rest = string.length()>1 ? string.substring(1) : "";
		return first + rest;
	}

}
