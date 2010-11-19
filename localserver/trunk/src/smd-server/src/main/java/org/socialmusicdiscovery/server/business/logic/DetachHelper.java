package org.socialmusicdiscovery.server.business.logic;

import org.hibernate.collection.PersistentCollection;

import java.lang.reflect.Field;
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
        for (T object : objects) {
            result.add(createDetachedCopy(object, cache));
        }
        return result;
    }

    private static <T> T createDetachedCopy(T object, Map<Object, Object> cache) {
        if (object == null) {
            return null;
        }
        T copy = (T) cache.get(object);
        if (copy != null) {
            return null;
        }
        if (Collection.class.isAssignableFrom(object.getClass())) {
            return (T) createDetachedCollectionCopy((Collection) object, cache);
        }
        try {
            copy = (T) object.getClass().newInstance();
            ArrayList<Field> fields = new ArrayList(Arrays.asList(object.getClass().getDeclaredFields()));
            Class cls = object.getClass().getSuperclass();
            while (!cls.equals(Object.class)) {
                fields.addAll(Arrays.asList(cls.getDeclaredFields()));
                cls = cls.getSuperclass();
            }
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    if (field.isAccessible()) {
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
        return createDetachedCopy(object, new HashMap<Object, Object>());
    }
}
