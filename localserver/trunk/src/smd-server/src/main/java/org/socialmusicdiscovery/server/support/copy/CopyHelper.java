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

package org.socialmusicdiscovery.server.support.copy;

import org.hibernate.collection.PersistentCollection;
import org.hibernate.collection.PersistentMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Helper class to make it easy to clone and merge objects of the same class
 */
public class CopyHelper {
    /**
     * Object cache, this is used to get the toObject to ensure there is only one instead of each object
     */
    private Cache objectCache;

    /**
     * Cache interface that should be implemented if you like to ensure that each Object instance only exists once
     */
    public static interface Cache {
        /**
         * Get object from cache
         *
         * @param cacheKey The cache key for the object to get from the cache
         * @return The cached object or null if it didn't exist
         */
        Object load(Object cacheKey);

        /**
         * Store object in cache
         *
         * @param cacheKey The cache key of the object
         * @param object   The object to store in the cache
         */
        void store(Object cacheKey, Object object);
    }

    public static class NoCache implements Cache {
        @Override
        public Object load(Object cacheKey) {
            return null;
        }

        @Override
        public void store(Object cacheKey, Object object) {
        }
    }

    /**
     * Create a new instance with no caching besides during a single copy operation
     */
    public CopyHelper() {
        objectCache = new NoCache();
    }

    /**
     * Create a new instance with a custom cache implementation. This can be used to ensure that you only get one instance of each object
     *
     * @param cacheImplementation
     */
    public CopyHelper(Cache cacheImplementation) {
        objectCache = cacheImplementation;
    }

    private <T> Collection<T> createDetachedCollectionCopy(Collection<T> toObjects, Collection<T> fromObjects, Map<Object, Object> cache, Class onlyAnnotatedWith, boolean onlyLoadedData) {
        if (onlyLoadedData && fromObjects instanceof PersistentCollection) {
            if (!((PersistentCollection) fromObjects).wasInitialized()) {
                return null;
            }
        }
        Collection<T> result;
        if (toObjects != null) {
            result = toObjects;
            result.clear();
        } else if (SortedSet.class.isAssignableFrom(fromObjects.getClass())) {
            result = new TreeSet<T>(((SortedSet<T>) fromObjects).comparator());
        } else if (Set.class.isAssignableFrom(fromObjects.getClass())) {
            result = new HashSet<T>(fromObjects.size());
        } else if (List.class.isAssignableFrom(fromObjects.getClass())) {
            result = new ArrayList<T>(fromObjects.size());
        } else {
            throw new RuntimeException("Unsupported collection type: " + fromObjects.getClass());
        }
        cache.put(fromObjects, result);
        for (T object : fromObjects) {
            result.add(copyObject(null, object, cache, onlyAnnotatedWith, onlyLoadedData));
        }
        return result;
    }

    private <K, T> Map<K, T> createDetachedMapCopy(Map<K, T> toObjects, Map<K, T> fromObjects, Map<Object, Object> cache, Class onlyAnnotatedWith, boolean onlyLoadedData) {
        if (onlyLoadedData && fromObjects instanceof PersistentMap) {
            if (!((PersistentMap) fromObjects).wasInitialized()) {
                return null;
            }
        }
        Map<K, T> result;
        if (toObjects != null) {
            result = toObjects;
            result.clear();
        } else if (SortedMap.class.isAssignableFrom(fromObjects.getClass())) {
            result = new TreeMap<K, T>(((SortedMap<K, T>) fromObjects).comparator());
        } else if (Map.class.isAssignableFrom(fromObjects.getClass())) {
            result = new HashMap<K, T>(fromObjects.size());
        } else {
            throw new RuntimeException("Unsupported map type: " + fromObjects.getClass());
        }
        cache.put(fromObjects, result);
        for (Map.Entry object : fromObjects.entrySet()) {
            K key = (K) copyObject(null, object.getKey(), cache, onlyAnnotatedWith, onlyLoadedData);
            T value = (T) copyObject(null, object.getValue(), cache, onlyAnnotatedWith, onlyLoadedData);
            result.put(key, value);
        }
        return result;
    }

    private <T> T copyObject(T toObject, T fromObject, Map<Object, Object> cache, Class onlyAnnotatedWith, boolean onlyLoadedData) {
        if (fromObject == null) {
            return null;
        }
        T copy = (T) cache.get(fromObject);
        if (copy != null) {
            return copy;
        }
        if (toObject == null) {
            // Try to get from object cache
            toObject = (T) objectCache.load(fromObject);
        }

        if (Collection.class.isAssignableFrom(fromObject.getClass())) {
            return (T) createDetachedCollectionCopy((Collection) toObject, (Collection) fromObject, cache, onlyAnnotatedWith, onlyLoadedData);
        } else if (Map.class.isAssignableFrom(fromObject.getClass())) {
            return (T) createDetachedMapCopy((Map) toObject, (Map) fromObject, cache, onlyAnnotatedWith, onlyLoadedData);
        } else if (fromObject.getClass().isPrimitive() ||
                fromObject.getClass().equals(String.class) ||
                fromObject.getClass().equals(Integer.class) ||
                fromObject.getClass().equals(Long.class) ||
                fromObject.getClass().equals(Date.class) ||
                fromObject.getClass().equals(Double.class) ||
                fromObject.getClass().equals(Float.class)) {
            return fromObject;
        }
        try {
            ArrayList<Field> fields = new ArrayList<Field>();
            Class cls = fromObject.getClass();
            while (!cls.equals(Object.class)) {
                if (onlyAnnotatedWith != null) {
                    for (Field field : cls.getDeclaredFields()) {
                        if (field.isAnnotationPresent(onlyAnnotatedWith)) {
                            fields.add(field);
                        }
                    }
                } else {
                    fields.addAll(Arrays.asList(cls.getDeclaredFields()));
                }
                cls = cls.getSuperclass();
            }
            if (fields.size() > 0) {
                if (toObject == null) {
                    copy = (T) fromObject.getClass().newInstance();
                    objectCache.store(fromObject, copy);
                } else {
                    copy = toObject;
                }
                cache.put(fromObject, copy);
                for (Field field : fields) {
                    try {
                        field.setAccessible(true);
                        if (!Modifier.isFinal(field.getModifiers())) {
                            if (field.getType().isPrimitive() ||
                                    field.getType().equals(String.class) ||
                                    field.getType().equals(Integer.class) ||
                                    field.getType().equals(Long.class) ||
                                    field.getType().equals(Date.class) ||
                                    field.getType().equals(Double.class) ||
                                    field.getType().equals(Float.class)) {
                                field.set(copy, field.get(fromObject));
                            } else if (Collection.class.isAssignableFrom(field.getType())) {
                                if (field.get(fromObject) != null) {
                                    field.set(copy, createDetachedCollectionCopy((Collection) field.get(copy), (Collection) field.get(fromObject), cache, onlyAnnotatedWith, onlyLoadedData));
                                } else {
                                    field.set(copy, null);
                                }
                            } else if (field.get(fromObject) != null) {
                                field.set(copy, copyObject(field.get(copy), field.get(fromObject), cache, onlyAnnotatedWith, onlyLoadedData));
                            } else {
                                field.set(copy, null);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return copy;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Merge the contents of a new object into an existing object
     *
     * @param toObject   The existing object to merge into
     * @param fromObject The new object which new values should be taken from
     * @return The updated object, this is the same instance as toObject and is just provided for convenience
     */
    public <T> T mergeInto(T toObject, T fromObject) {
        Map<Object, Object> cache = new HashMap<Object, Object>();
        T result = copyObject(toObject, fromObject, cache, null, false);
        cache.clear();
        return result;
    }

    /**
     * Merge the contents of a new object into an existing object but limit the merging to attribute with a specific annotation. Attributes
     * without this annotation will be untouched and keep their old values.
     *
     * @param toObject          The existing object to merge into
     * @param fromObject        The new object which new values should be taken from
     * @param onlyAnnotatedWith The annotation a field has to have to be part of the merging
     * @return The updated object, this is the same instance as toObject and is just provided for convenience
     */
    public <T> T mergeInto(T toObject, T fromObject, Class onlyAnnotatedWith) {
        Map<Object, Object> cache = new HashMap<Object, Object>();
        T result = copyObject(toObject, fromObject, cache, onlyAnnotatedWith, false);
        cache.clear();
        return result;
    }

    /**
     * Creates a copy of the specified object, the copying will not copy collections and maps in persistent JPA entities unless they have already
     * been loaded. If you like to also load these collections and maps, use {@link #copy(Object)} instead.
     *
     * @param object The object that should be copied
     * @return The new instance of the object
     */
    public <T> T detachedCopy(T object) {
        Map<Object, Object> cache = new HashMap<Object, Object>();
        T result = copyObject(null, object, cache, null, true);
        cache.clear();
        return result;
    }

    /**
     * Creates a copy of the specified object, the copying will not copy collections and maps in persistent JPA entities unless they have already
     * been loaded. If you like to also load these collections and maps, use {@link #copy(Object, Class)} instead.
     * The copying is limited to fields with the specified annotation.
     *
     * @param object            The object that should be copied
     * @param onlyAnnotatedWith The annotation a field has to have to be part of the merging
     * @return The new instance of the object
     */
    public <T> T detachedCopy(T object, Class onlyAnnotatedWith) {
        Map<Object, Object> cache = new HashMap<Object, Object>();
        T result = copyObject(null, object, cache, onlyAnnotatedWith, true);
        cache.clear();
        return result;
    }

    /**
     * Creates a copy of the specified object. Note that if you have used {@link #CopyHelper(Cache)} constructor
     * to provide your own cache implementation, this operation will do the same thing as {@link #mergeInto(Object, Object)} .
     *
     * @param object The object that should be copied
     * @return The new instance of the object
     */
    public <T> T copy(T object) {
        Map<Object, Object> cache = new HashMap<Object, Object>();
        T result = copyObject(null, object, cache, null, false);
        cache.clear();
        return result;
    }

    /**
     * Creates a copy of the specified object. Note that if you have used {@link #CopyHelper(Cache)} constructor
     * to provide your own cache implementation, this operation will do the same thing as {@link #mergeInto(Object, Object, Class)} .
     *
     * @param object            The object that should be copied
     * @param onlyAnnotatedWith The annotation a field has to have to be part of the merging
     * @return The new instance of the object
     */
    public <T> T copy(T object, Class onlyAnnotatedWith) {
        Map<Object, Object> cache = new HashMap<Object, Object>();
        T result = copyObject(null, object, cache, onlyAnnotatedWith, false);
        cache.clear();
        return result;
    }

}
