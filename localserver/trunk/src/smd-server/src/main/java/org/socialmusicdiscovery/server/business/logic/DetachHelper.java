package org.socialmusicdiscovery.server.business.logic;

import org.hibernate.collection.PersistentCollection;
import org.hibernate.collection.PersistentMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class DetachHelper {
    private static <T> Collection<T> createDetachedCollectionCopy(Collection<T> objects, Map<Object, Object> cache) {
        if (objects instanceof PersistentCollection) {
            if (!((PersistentCollection) objects).wasInitialized()) {
                return null;
            }
        }
        Collection<T> result;
        if (SortedSet.class.isAssignableFrom(objects.getClass())) {
            result = new TreeSet<T>(((SortedSet<T>) objects).comparator());
        } else if (Set.class.isAssignableFrom(objects.getClass())) {
            result = new HashSet<T>(objects.size());
        } else if (List.class.isAssignableFrom(objects.getClass())) {
            result = new ArrayList<T>(objects.size());
        } else {
            throw new RuntimeException("Unsupported collection type: " + objects.getClass());
        }
        cache.put(objects, result);
        for (T object : objects) {
            result.add(createDetachedCopy(object, cache));
        }
        return result;
    }

    private static <K, T> Map<K, T> createDetachedMapCopy(Map<K, T> objects, Map<Object, Object> cache) {
        if (objects instanceof PersistentMap) {
            if (!((PersistentMap) objects).wasInitialized()) {
                return null;
            }
        }
        Map<K, T> result;
        if (SortedMap.class.isAssignableFrom(objects.getClass())) {
            result = new TreeMap<K, T>(((SortedMap<K, T>) objects).comparator());
        } else if (Map.class.isAssignableFrom(objects.getClass())) {
            result = new HashMap<K, T>(objects.size());
        } else {
            throw new RuntimeException("Unsupported map type: " + objects.getClass());
        }
        cache.put(objects, result);
        for (Map.Entry object : objects.entrySet()) {
            K key = (K) createDetachedCopy(object.getKey(), cache);
            T value = (T) createDetachedCopy(object.getValue(), cache);
            result.put(key, value);
        }
        return result;
    }

    private static <T> T createDetachedCopy(T object, Map<Object, Object> cache) {
        if (object == null) {
            return null;
        }
        T copy = (T) cache.get(object);
        if (copy != null) {
            return copy;
        }
        if (Collection.class.isAssignableFrom(object.getClass())) {
            return (T) createDetachedCollectionCopy((Collection) object, cache);
        } else if (Map.class.isAssignableFrom(object.getClass())) {
            return (T) createDetachedMapCopy((Map) object, cache);
        } else if (object.getClass().isPrimitive() ||
                object.getClass().equals(String.class) ||
                object.getClass().equals(Integer.class) ||
                object.getClass().equals(Long.class) ||
                object.getClass().equals(Date.class) ||
                object.getClass().equals(Double.class) ||
                object.getClass().equals(Float.class)) {
            return object;
        }
        try {
            copy = (T) object.getClass().newInstance();
            cache.put(object, copy);
            ArrayList<Field> fields = new ArrayList(Arrays.asList(object.getClass().getDeclaredFields()));
            Class cls = object.getClass().getSuperclass();
            while (!cls.equals(Object.class)) {
                fields.addAll(Arrays.asList(cls.getDeclaredFields()));
                cls = cls.getSuperclass();
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
                            field.set(copy, field.get(object));
                        } else if (Collection.class.isAssignableFrom(field.getType())) {
                            if (field.get(object) != null) {
                                field.set(copy, createDetachedCollectionCopy((Collection) field.get(object), cache));
                            }
                        } else if (field.get(object) != null) {
                            field.set(copy, createDetachedCopy(field.get(object), cache));
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

    public static <T> T createDetachedCopy(T object) {
        Map<Object, Object> cache = new HashMap<Object, Object>();
        T result = createDetachedCopy(object, cache);
        cache.clear();
        return result;
    }
}
