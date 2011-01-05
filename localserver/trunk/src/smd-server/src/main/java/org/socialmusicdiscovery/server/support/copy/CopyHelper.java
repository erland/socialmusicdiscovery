package org.socialmusicdiscovery.server.support.copy;

import org.hibernate.collection.PersistentCollection;
import org.hibernate.collection.PersistentMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class CopyHelper {
    private static <T> Collection<T> createDetachedCollectionCopy(Collection<T> toObjects, Collection<T> fromObjects, Map<Object, Object> cache, Class onlyAnnotatedWith) {
        if (fromObjects instanceof PersistentCollection) {
            if (!((PersistentCollection) fromObjects).wasInitialized()) {
                return null;
            }
        }
        Collection<T> result;
        if(toObjects!=null) {
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
            result.add(createDetachedCopy(null, object, cache, onlyAnnotatedWith));
        }
        return result;
    }

    private static <K, T> Map<K, T> createDetachedMapCopy(Map<K, T> toObjects, Map<K, T> fromObjects, Map<Object, Object> cache, Class onlyAnnotatedWith) {
        if (fromObjects instanceof PersistentMap) {
            if (!((PersistentMap) fromObjects).wasInitialized()) {
                return null;
            }
        }
        Map<K, T> result;
        if(toObjects!=null) {
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
            K key = (K) createDetachedCopy(null, object.getKey(), cache, onlyAnnotatedWith);
            T value = (T) createDetachedCopy(null, object.getValue(), cache, onlyAnnotatedWith);
            result.put(key, value);
        }
        return result;
    }

    private static <T> T createDetachedCopy(T toObject, T fromObject, Map<Object, Object> cache, Class onlyAnnotatedWith) {
        if (fromObject == null) {
            return null;
        }
        T copy = (T) cache.get(fromObject);
        if (copy != null) {
            return copy;
        }
        if (Collection.class.isAssignableFrom(fromObject.getClass())) {
            return (T) createDetachedCollectionCopy((Collection) toObject, (Collection) fromObject, cache, onlyAnnotatedWith);
        } else if (Map.class.isAssignableFrom(fromObject.getClass())) {
            return (T) createDetachedMapCopy((Map) toObject, (Map) fromObject, cache, onlyAnnotatedWith);
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
                if(onlyAnnotatedWith!=null) {
                    for (Field field : cls.getDeclaredFields()) {
                        if(field.isAnnotationPresent(onlyAnnotatedWith)) {
                            fields.add(field);
                        }
                    }
                }else {
                    fields.addAll(Arrays.asList(cls.getDeclaredFields()));
                }
                cls = cls.getSuperclass();
            }
            if(onlyAnnotatedWith==null||fields.size()>0) {
                if(toObject==null) {
                    copy = (T) fromObject.getClass().newInstance();
                }else {
                    copy = toObject;
                }
                cache.put(fromObject, copy);
            }
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
                                field.set(copy, createDetachedCollectionCopy((Collection) field.get(copy), (Collection) field.get(fromObject), cache, onlyAnnotatedWith));
                            }else {
                                field.set(copy, null);
                            }
                        } else if (field.get(fromObject) != null) {
                            field.set(copy, createDetachedCopy(field.get(copy), field.get(fromObject), cache, onlyAnnotatedWith));
                        } else {
                            field.set(copy, null);
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            return copy;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T mergeInto(T toObject, T fromObject) {
        Map<Object, Object> cache = new HashMap<Object, Object>();
        T result = createDetachedCopy(toObject, fromObject, cache, null);
        cache.clear();
        return result;
    }

    public static <T> T mergeInto(T toObject, T fromObject, Class onlyAnnotatedWith) {
        Map<Object, Object> cache = new HashMap<Object, Object>();
        T result = createDetachedCopy(toObject, fromObject, cache, onlyAnnotatedWith);
        cache.clear();
        return result;
    }

    public static <T> T createDetachedCopy(T object) {
        Map<Object, Object> cache = new HashMap<Object, Object>();
        T result = createDetachedCopy(null, object, cache, null);
        cache.clear();
        return result;
    }

    public static <T> T createDetachedCopy(T object, Class onlyAnnotatedWith) {
        Map<Object, Object> cache = new HashMap<Object, Object>();
        T result = createDetachedCopy(null, object, cache, onlyAnnotatedWith);
        cache.clear();
        return result;
    }
}
