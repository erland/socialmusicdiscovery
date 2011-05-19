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
