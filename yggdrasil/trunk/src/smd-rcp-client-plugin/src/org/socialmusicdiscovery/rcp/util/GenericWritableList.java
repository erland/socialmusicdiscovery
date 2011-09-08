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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.WritableList;

/**
 * <p>
 * Neither the standard {@link IObservableCollection} interface nor any of its
 * subtypes or implementation classes are generic. Unfortunately, the Gson
 * serializer requires generic collections to instantiate, and we want to
 * convert standard collections into observable sets without this error:
 * <code>java.lang.IllegalArgumentException: Collection objects need to be 
 * parameterized unless you use a custom serializer. Use the com.google.gson.reflect.TypeToken 
 * to extract the ParameterizedType.</code>
 * </p>
 * 
 * <p>
 * Unfortunately, we cannot make this class behave in a "for each" loop since
 * the superclass implements a standard {@link List} (no generic parameter); we
 * cannot state that it implements <code>Iterator&lt;T&gt;</code>. Alas, to loop
 * over this list, caller must cast element type just as when calling the
 * superclass.
 * </p>
 * 
 * @author Peer Törngren
 */
public class GenericWritableList<T> extends WritableList {

	public GenericWritableList() {
		super();
	}

	public GenericWritableList(Collection<T> c, Object elementType) {
		super(c, elementType);
	}

	public GenericWritableList(List<T> c, Object elementType) {
		super(c, elementType);
	}

	public GenericWritableList(Realm realm, Collection<T> c, Object elementType) {
		super(realm, c, elementType);
	}
	
	public GenericWritableList(Realm realm, List<T> c, Object elementType) {
		super(realm, c, elementType);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<T> iterator() {
		return super.iterator();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] toArray() {
		return (T[]) super.toArray();
	}
	@SuppressWarnings("unchecked")
	@Override
	public T[] toArray(Object[] a) {
		return (T[]) super.toArray(a);
	}

}
