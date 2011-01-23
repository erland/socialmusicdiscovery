package org.socialmusicdiscovery.server.support.format.provider;

import org.socialmusicdiscovery.server.support.format.DataProvider;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Attribute based data provider that reads values from object attributes using reflection.
 * <p/>
 * - A value like this: %object1.attr1.attr2
 * - Will use the object named "object1" and read its "attr1" attribute and inside that attribute get the value from the "attr2" attribute.
 */
public class AttributeDataProvider implements DataProvider {
    private Map<String, Object> objects = new HashMap<String, Object>();

    public AttributeDataProvider() {
    }

    /**
     * @inheritDoc
     */
    @Override
    public void init(Map<String, Object> objects) {
        this.objects = objects;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Object getValue(String fieldName) {
        Object item = null;
        String objectName = null;

        // Get the reference object
        if (fieldName.contains(".")) {
            objectName = fieldName.substring(1, fieldName.indexOf("."));
            fieldName = fieldName.substring(objectName.length() + 2);
            item = objects.get(objectName);
        } else if (fieldName.length() > 1) {
            objectName = fieldName.substring(1);
            fieldName = null;
            item = objects.get(objectName);
        }
        if (item != null) {
            // Return object if now attribute was specified
            if (fieldName == null) {
                return item;
            }

            // Parse attribute list from provided field name
            StringTokenizer tokens = new StringTokenizer(fieldName, ".");
            List<String> attributes = new ArrayList<String>(tokens.countTokens());
            while (tokens.hasMoreTokens()) {
                attributes.add(tokens.nextToken());
            }

            // Get attribute value
            return getVariableValue(item, attributes);
        }
        return null;
    }

    /**
     * Get the attribute value from the provided object based on the attribute hierarchy
     *
     * @param value      The object to get attribute from
     * @param attributes The list of attributes that leads to the attribute you want
     * @return The attribute value or null if the attribute couldn't be found
     */
    private Object getVariableValue(Object value, List<String> attributes) {
        while (attributes.size() > 0 && value != null) {
            String attr = attributes.remove(0);
            Field field = null;
            Class cls = value.getClass();
            while (!cls.isAssignableFrom(Object.class)) {
                try {
                    field = cls.getDeclaredField(attr);
                    break;
                } catch (NoSuchFieldException e) {
                    // Ignore, just continue searching, it might be inside super class
                }
                cls = cls.getSuperclass();
            }
            if (field != null) {
                field.setAccessible(true);
                try {
                    value = field.get(value);
                    if(Collection.class.isAssignableFrom(field.getType())) {
                        Iterator it = ((Collection)value).iterator();
                        if(it.hasNext()) {
                            value = it.next();
                        }else {
                            value = null;
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace(System.err);
                    value = null;
                }
            } else {
                value = null;
            }
        }
        if (value != null) {
            return value;
        } else {
            return null;
        }
    }
}
