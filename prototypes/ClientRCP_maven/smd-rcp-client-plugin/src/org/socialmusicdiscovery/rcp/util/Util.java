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

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Some general utilities.
 * 
 * @author Peer Törngren
 *
 */
public final class Util {

	private Util() {}

	public static <T> Set<T> asSet(T... elements) {
		Set<T> set = new HashSet<T>();
		for (T element : elements) {
			set.add(element);
		}
		return set;
	}

	/**
	 * Internal convenience. Use for debugging/development only.
	 * @param secondsNotMilliseconds
	 */
	public static void sleep(int secondsNotMilliseconds) {
		try {
			Thread.sleep(secondsNotMilliseconds*1000);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted", e);  //$NON-NLS-1$
		}
	}

	/**
	 * Convenience method to compare two {@link Comparable} objects, respecting
	 * that either one (or both) may be <code>null</code>.
	 * 
	 * @param c1
	 * @param c2
	 * @return int
	 * @see Comparator
	 */
	@SuppressWarnings("unchecked")
	public static int compare(Comparable c1, Comparable c2) {
		if (c1==null) {
			return c2==null ? 0 : -1;
		} else if (c2==null) {
			return 1;
		}
		return c1.compareTo(c2);
	}
	
	/**
	 * Convenience method to check for <code>null</code> or empty string.
	 * @param s
	 * @return <code>true</code> if string is <code>null</code> or has no non-blank character
	 * @see #isSet(String)
	 */
	public static boolean isEmpty(String s) {
		return s==null || s.trim().isEmpty();
	}
	
	/**
	 * Convenience method to check for a string with at least one significant character.
	 * @param s
	 * @return <code>true</code> if string is not <code>null</code> and has at least 1 non-blank character
	 * @see #isEmpty(String)
	 */
	public static boolean isSet(String s) {
		return !isEmpty(s);
	}
}
