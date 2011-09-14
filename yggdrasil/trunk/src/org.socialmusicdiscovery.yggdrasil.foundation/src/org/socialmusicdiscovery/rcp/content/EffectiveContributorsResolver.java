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

package org.socialmusicdiscovery.rcp.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.socialmusicdiscovery.server.business.model.core.Contributor;

/**
 * <p>Resolves effective contributors by processing a list of contributor
 * collections in the order they were supplied, with most significant collection
 * first, and least significant collection last.</p>
 * 
 * <p>TODO this is just a stub. Need to handle updates in collections.</p>
 * 
 * @author Peer Törngren
 */
public class EffectiveContributorsResolver<T extends Contributor> {

	private Set<T> effectiveContributors;

	public EffectiveContributorsResolver(Collection<T>... contributorSets) {
		this(Arrays.asList(contributorSets));
	}

	public EffectiveContributorsResolver(List<Collection<T>> contributorSets) {
		List<Map<String, Set<T>>> list = compileTypesPerCollection(contributorSets);
		effectiveContributors = getEffectiveContributorsPerType(list);
	}


	public Set<T> getEffectiveContributors() {
		return effectiveContributors;
	}
	
	private List<Map<String, Set<T>>> compileTypesPerCollection(List<Collection<T>> contributorCollections) {
		List<Map<String, Set<T>>> list = new ArrayList<Map<String, Set<T>>>();
		for (Collection<T> collection : contributorCollections) {
			list.add(compile(collection));
		}
		return list;
	}

	private Map<String, Set<T>> compile(Collection<T> contributors) {
		Map<String, Set<T>> result = new HashMap<String, Set<T>>();
		for (T contributor : contributors) {
			String type = contributor.getType();
			Set<T> typeSet = result.get(type);
			if (typeSet==null) {
				typeSet = new HashSet<T>();
				result.put(type, typeSet);
			}
			typeSet.add(contributor);
		}
		return result;
	}
	
	private Set<T> getEffectiveContributorsPerType(List<Map<String, Set<T>>> list) {
		Set<String> types= new HashSet<String>();
		Set<T> result = new HashSet<T>();
		for (Map<String, Set<T>> map : list) {
			for (Map.Entry<String, Set<T>> entry : map.entrySet()) {
				String type = entry.getKey();
				if (!types.contains(type)) {
					types.add(type);
					result.addAll(entry.getValue());
				}
			}
		}
		return result;
	}
}
