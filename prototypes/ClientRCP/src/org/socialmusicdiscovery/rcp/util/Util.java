package org.socialmusicdiscovery.rcp.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Some general utilities.
 * 
 * @author Peer Törngren
 *
 */
public class Util {

	private Util() {}

	public static <T> Set<T> asSet(T... elements) {
		Set<T> set = new HashSet<T>();
		for (T element : elements) {
			set.add(element);
		}
		return set;
	}

//	public static Set asSet(Object... elements) {
//		Set set = new HashSet();
//		for (Object element : elements) {
//			set.add(element);
//		}
//		return set;
//	}

}
