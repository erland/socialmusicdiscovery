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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.socialmusicdiscovery.rcp.error.FatalApplicationException;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import com.google.gson.annotations.Expose;

/**
 * <p>
 * A cache with the primary responsibility to help the {@link DataSource} to
 * 'inflate' shallow client objects (used in list views) with remaining data
 * from fully loaded server objects, ensuring that we maintain unique instances
 * on the client side. Uses a cache to keep track of all mutable objects. For
 * the time being (and foreseeable future?), 'mutable objects' translates to
 * subclasses of {@link SMDIdentity}, since all objects we get from the server
 * are of this type. Furthermore, it will only track attributes marked with the
 * GSON @Expose annotation, again since this all we get from the server.
 * </p>
 * 
 * <p>
 * Implementation note 1: this class is preliminary. It is not yet thoroughly
 * tested, and it is expected to evolve over time; the exact requirements are
 * yet to be discovered. In particular, I suspect problems with duplicated
 * objects, missing attributes or concurrency issues when several objects are
 * loaded in parallel on separate UI threads. Nothing is thread safe.
 * </p>
 * 
 * <p>
 * Implementation note 2: there are some weird and ugly type-casting and
 * generics here. Reason is primarily that I don't know how to handle this
 * better (i.e. it's not intentional) ... all objects we deal with in the cache
 * are {@link ObservableEntity} instances, but fields and interface method
 * signatures declare {@link SMDIdentity} subtypes. We need to check for this
 * type in field declarations, but push the resulting value into the cache as an
 * {@link ObservableEntity}. This can (and should) probably be fixed at some
 * point, the current implementation is confusing.
 * </p>
 * 
 * @author Peer Törngren
 */
/* package */class DataCache {
	
	/** Simple 'visitor' interface to reuse logic to select fields. */
	private interface FieldFilter {
		boolean accept(Field field);
	}
	
    /** Accepts exposed fields. */
    private class MyAnnotatedFieldFilter implements FieldFilter {
		@Override
		public boolean accept(Field field) {
			boolean isAnnotated = field.isAnnotationPresent(Expose.class);
			assert !(isAnnotated && Modifier.isFinal(field.getModifiers())) : "Final field exposed!? "+field; //$NON-NLS-1$
			assert !(isAnnotated && Modifier.isStatic(field.getModifiers())) : "Static field exposed!? "+field; //$NON-NLS-1$
			return isAnnotated ;
		}
	}

    /** Accepts exposed fields that hold {@link Collection}s or {@link SMDIdentity} instances. */
    private class MyEntityFieldFilter extends MyAnnotatedFieldFilter {
		@Override
		public boolean accept(Field field) {
			return super.accept(field) && isCachableType((Class) field.getType());
		}

		private boolean isCachableType(Class type) {
			// TODO check for collections of the generic type (i.e. only SMDIdentity collections)
			return SMDIdentity.class.isAssignableFrom(type) || Collection.class.isAssignableFrom(type);
		}
	}

	/**
	 * <p>
	 * Cache is used to ensure that client has only one instance of each server
	 * object. We could use a {@link WeakHashMap} in an attempt to let go of
	 * instance no longer held by anyone, but this is probably not enough to
	 * free up resources - as long as the navigator holds the list of root
	 * objects, all "inflated" objects (and all children and offsprings at any
	 * level) will most likely live forever. If memory consumption becomes a
	 * problem, we may have to invent some way to "deflate" objects that are
	 * only visible in the navigator (perhaps by using some kind of weak link in
	 * the navigator?).
	 * </p>
	 * 
	 * <p>
	 * Note: using a {@link WeakHashMap} causes problems in current
	 * implementation; objects can get dropped and reloaded. One example is that
	 * a loaded track is no longer in the cache when its owning release is
	 * deleted. Not thoroughly investigated, but problem went away when
	 * switching from {@link WeakHashMap} to regular {@link HashMap}.
	 * </p>
	 */
	private final Map<String, SMDIdentity> cache = new HashMap<String, SMDIdentity>();

	/* Internal, use primarily for assertions (not only by this class) */
	/* package */ boolean contains(SMDIdentity anObject) {
		String key = anObject.getId();
		return cache.containsKey(key) && cache.get(key)==anObject;
	}
	
	/* Internal, use primarily for assertions (not only by this class) */
	/* package */ boolean containsAll(Collection<? extends SMDIdentity> someObjects) {
		for (SMDIdentity anObject : someObjects) {
			if (!contains(anObject)) {
				return false;
			}
		}
		return true;
	}
	
	// TODO fix generics, eliminate warning
	/* package */ @SuppressWarnings("unchecked")
	<T extends SMDIdentity> AbstractObservableEntity<T> getOrStore(T serverObjectOrNull) {
    	if (serverObjectOrNull==null) {
    		return null;
    	}
    	
    	// handle main object
    	String key = serverObjectOrNull.getId();
		if (cache.get(key)==null) {
    		cache.put(key, serverObjectOrNull);
			ObservableEntity<T> cachedObject = (ObservableEntity<T>) serverObjectOrNull;
			// Recursively replace all referenced entities with cached values,
			// or update cache with these new values. Note: this is really just
			// a trick to replace all references in a recently fetched server
			// object with cached value if we have it; it reads each reference
			// from itself and writes back either the same value or a cached
			// equivalence. Check the details of the method:
			// 		copyField(Field, SMDIdentity, ObservableEntity)
    		deepMerge(serverObjectOrNull, cachedObject, new MyEntityFieldFilter());
    	}
    	return (AbstractObservableEntity<T>) cache.get(key);
    }
  
    /* package */ <T extends SMDIdentity> void merge(T serverObject, ObservableEntity<T> clientObject) {
		MyAnnotatedFieldFilter filter = new MyAnnotatedFieldFilter();
		deepMerge(serverObject, clientObject, filter);
	}

    /* package */ void add(ObservableEntity entity) {
    	assert !cache.containsKey(entity.getId()) : "Entity already cached: " + entity;
    	cache.put(entity.getId(), entity);
	}

    /* package */  void delete(ObservableEntity entity) {
    	assert contains(entity) : "Entity not cached: " + entity;
    	cache.remove(entity.getId());
	}
    
    /* package */  void clear() {
		this.cache.clear();
	}

	private <T extends SMDIdentity> void deepMerge(T from, ObservableEntity<T> to, FieldFilter filter) {
		for (Field f : getFields(to, filter)) {
			try {
				copyField(f, from, to);
			} catch (IllegalAccessException e) {
				// should not happen since we set accessible to disable checking
				throw new FatalApplicationException("Failed to update field "+f+" from "+from+" to " + to, e);  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
			} 
		}
	}

	private <T extends SMDIdentity> void copyField(Field f, T from, ObservableEntity<T> to) throws IllegalAccessException {
		boolean wasAccessible = f.isAccessible();
		f.setAccessible(true);
		Class type = f.getType();
		
		if (Collection.class.isAssignableFrom(type)) {
			copyCollectionValue(f, from, to);
		} else if (SMDIdentity.class.isAssignableFrom(type)) {
			copyEntityValue(f, from, to );
		} else {
			copyPlainValue(f, from, to);
		}
		
		f.setAccessible(wasAccessible);
	}

	@SuppressWarnings("unchecked")
	private <T extends SMDIdentity> void copyCollectionValue(Field f, T fromInstance, ObservableEntity<T> toInstance) throws IllegalAccessException {
		assert f.get(fromInstance)!=null : "Source collection is null. Field: " +f; 
		assert f.get(toInstance)!=null : "Target collection is null. Field: " +f+ " (cannot (yet) handle null target collections. Client must initialize with an empty collection of appropriate type)";
		Collection fromCollection = new ArrayList((Collection) f.get(fromInstance));  
		Collection toCollection = (Collection) f.get(toInstance);

		// run
		toCollection.clear();
		if (fromCollection!=null) {
			for (Object fromCollectionElement : fromCollection) {
				if (fromCollectionElement instanceof SMDIdentity) {
					Object toCollectionElement = getOrStore((SMDIdentity) fromCollectionElement);
					toCollection.add(toCollectionElement);
				} else {
					toCollection.add(fromCollectionElement);
				}
			}
		}
	}

	private void copyEntityValue(Field f, SMDIdentity fromInstance, ObservableEntity toInstance) {
		SMDIdentity toValue = getFieldValue(f, toInstance);
		SMDIdentity fromValue = getFieldValue(f, fromInstance);
		SMDIdentity cachedValue = getOrStore(fromValue); 
		if (toValue!=cachedValue) {
			if(cachedValue==null || f.getType().isAssignableFrom(cachedValue.getClass())) {
				setFieldValue(f, toInstance, cachedValue);
			}else {
				setFieldValue(f, toInstance, toValue);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T getFieldValue(Field f, Object object) {
		try {
			return (T) f.get(object);
		} catch (IllegalAccessException e) {
			throw new FatalApplicationException("Cannot get value for field: "+f, e);  //$NON-NLS-1$
		}
	}

	private void setFieldValue(Field f, Object toInstance, Object newValue) {
		try {
			f.set(toInstance, newValue);
		} catch (IllegalAccessException e) {
			throw new FatalApplicationException("Could not set field value: "+f, e);  //$NON-NLS-1$
		}
	}

	private void copyPlainValue(Field f, SMDIdentity fromInstance, ObservableEntity toInstance) {
		Object newValue = getFieldValue(f, fromInstance);
		setFieldValue(f, toInstance, newValue);
	}

	private Set<Field> getFields(SMDIdentity entity, FieldFilter filter) {
//		This may or mat not provide more details? Also, it caches the descriptors which might save some time.
//		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(entity);
//		for (PropertyDescriptor descriptor : descriptors) {
//			String fieldName = descriptor.getName();
//		}
        Set<Field> fields = new HashSet<Field>();
        Class type = entity.getClass();
        while (type!=null) {
            for (Field field : type.getDeclaredFields()) {
                if (filter.accept(field)) {
                    fields.add(field);
                }
            }
            type = type.getSuperclass();
        }
        return fields;
	}

	public String dump() {
		List<SMDIdentity> values = new ArrayList<SMDIdentity>(cache.values());
		Collections.sort(values, new Comparator<SMDIdentity>() {
			@Override
			public int compare(SMDIdentity o1, SMDIdentity o2) {
				return String.valueOf(o1).compareTo(String.valueOf(o2));
			}
		});
		
		StringBuilder sb = new StringBuilder();
		for (SMDIdentity value : values) {
			sb.append("\t");
			sb.append(value);
			sb.append("/");
			sb.append(value.getId());
			sb.append("#");
			sb.append(value.hashCode());
			sb.append("\n");
		}
		return sb.toString();
	}

}