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

package org.socialmusicdiscovery.server.support.json;

import com.google.gson.*;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract JSON provider class to make it easy to provide JSON support for an interface by providing a mapping between the
 * interface class and the corresponding implementation class. The implementation of this provider is based on the Google Gson library.
 */
public abstract class AbstractJSONProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

    /**
     * Provides a JSON serializer for the specified implementation class
     */
    public static class ImplementationSerializer implements JsonSerializer {
        private Class implementationClass;

        public ImplementationSerializer(Class implementationClass) {
            this.implementationClass = implementationClass;
        }

        public JsonElement serialize(Object o, Type type, JsonSerializationContext jsonSerializationContext) {
            return jsonSerializationContext.serialize(o, implementationClass);
        }
    }

    /**
     * Provides a JSON deserializer for the specified implementation class
     */
    public static class ImplementationDeserializer implements JsonDeserializer {
        private Class implementationClass;

        public ImplementationDeserializer(Class implementationClass) {
            this.implementationClass = implementationClass;
        }

        public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return jsonDeserializationContext.deserialize(jsonElement, implementationClass);
        }
    }

    /**
     * Provides a JSON deserializer for the specified abstract implementation class
     */
    public static class AbstractImplementationDeserializer implements JsonDeserializer {
        private Class implementationClass;
        private Map<String,Class> objectTypeToClassMap;

        public AbstractImplementationDeserializer(Class implementationClass, Map<String,Class> objectTypeToClassMap) {
            this.implementationClass = implementationClass;
            this.objectTypeToClassMap = objectTypeToClassMap;
        }

        public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonElement classAttribute = jsonElement.getAsJsonObject().get("objectType");
            if(classAttribute==null) {
                throw new JsonParseException("Element related to abstract type "+implementationClass.getName()+" doesn't have the necessary objectType attribute");
            }
            Class clazz = objectTypeToClassMap.get(classAttribute.getAsString());
            if(clazz==null) {
                throw new JsonParseException("No class can be found for objectType="+classAttribute.getAsString());
            }
            if(!implementationClass.isAssignableFrom(clazz)) {
                throw new JsonParseException(clazz.getName() + " not compatible with "+implementationClass.getName());
            }
            return jsonDeserializationContext.deserialize(jsonElement, clazz);
        }
    }

    /**
     * Provides a JSON deserializer for the specified Collection based implementation class
     */
    public static class ImplementationCollectionDeserializer implements JsonDeserializer {
        private Class<? extends Collection> implementationClass;

        public ImplementationCollectionDeserializer(Class<? extends Collection> implementationClass) {
            this.implementationClass = implementationClass;
        }

        public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonNull()) {
              return null;
            }

            try {
                Collection collection = implementationClass.newInstance();
                if(!(type instanceof ParameterizedType)) {
                    throw new JsonParseException("Unable to deserialize "+type+" no element type information available");
                }
                Type childType = ((ParameterizedType) type).getActualTypeArguments()[0];
                for (JsonElement childElement : jsonElement.getAsJsonArray()) {
                  if (childElement == null || childElement.isJsonNull()) {
                    collection.add(null);
                  } else {
                    Object value = jsonDeserializationContext.deserialize(childElement, childType);
                    collection.add(value);
                  }
                }

                return collection;
            } catch (InstantiationException e) {
                throw new JsonParseException(e);
            } catch (IllegalAccessException e) {
                throw new JsonParseException(e);
            }
        }
    }

    /**
     * Provides a JSON instance creator for the specified implementation class. The implementation class must have a default constructor.
     */
    public static class ImplementationCreator implements InstanceCreator {
        private Class implementationClass;

        public ImplementationCreator(Class implementationClass) {
            this.implementationClass = implementationClass;
        }

        public Object createInstance(Type type) {
            try {
                return implementationClass.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    ;

    /**
     * Returns a map with interface classes as keys and implementation classes as values.
     * This method should be implemented by the sub class and based on its data type converters will be provided for:
     * - Creating an implementation class instance when an interface type is found
     * - Deserializing to an implementation class when an interface type is found
     * - Serializing from an implementation class when an interface type is found
     *
     * @return A map with interface/implementation class mapping
     */
    protected abstract Map<Class, Class> getConversionMap();

    protected Map<String, Class> getObjectTypeConversionMap() {
        return new HashMap<String,Class>();
    }

    /**
     * Google Gson GsonBuilder instance that will be used during JSON conversion
     */
    private GsonBuilder gsonBuilder;

    /**
     * Create a new JSON provider
     *
     * @param onlyExposed If true, only the attributes annotated with @Exposed will be serialized/deserialized
     */
    public AbstractJSONProvider(boolean onlyExposed) {
        this.gsonBuilder = new GsonBuilder();

        Map<Class, Class> converters = getConversionMap();

        if (onlyExposed) {
            this.gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        }

        for (Map.Entry<Class, Class> entry : converters.entrySet()) {
            if(!entry.getKey().equals(entry.getValue())) {
                gsonBuilder.registerTypeAdapter(entry.getKey(), new ImplementationSerializer(entry.getValue()));
            }
            if(!Modifier.isAbstract(entry.getValue().getModifiers())) {
                gsonBuilder.registerTypeAdapter(entry.getKey(), new ImplementationCreator(entry.getValue()));
            }
            if(Collection.class.isAssignableFrom(entry.getKey())) {
                gsonBuilder.registerTypeAdapter(entry.getKey(), new ImplementationCollectionDeserializer(entry.getValue()));
            }else {
                if(Modifier.isAbstract(entry.getValue().getModifiers())) {
                    gsonBuilder.registerTypeAdapter(entry.getKey(), new AbstractImplementationDeserializer(entry.getValue(),getObjectTypeConversionMap()));
                }else {
                    gsonBuilder.registerTypeAdapter(entry.getKey(), new ImplementationDeserializer(entry.getValue()));
                }
            }
        }
    }

    /**
     * @inheritDoc
     */
    public long getSize(Object t, Class type, Type genericType, Annotation[] annotations,
                        MediaType mediaType) {
        return -1;
    }

    /**
     * @inheritDoc
     */
    public void writeTo(Object t, Class type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        //TODO: Remove setPrettyPrinting later, for now it's useful during debugging
        entityStream.write(gsonBuilder.setPrettyPrinting().create().toJson(t).getBytes());

    }

    /**
     * @inheritDoc
     */
    public boolean isWriteable(Class type, Type genericType, Annotation[] annotations,
                               MediaType mediaType) {
        return true;
    }

    /**
     * @inheritDoc
     */
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    /**
     * @inheritDoc
     */
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        Reader entityReader = new InputStreamReader(entityStream);
        Type targetType;
        if (Collection.class.isAssignableFrom(type)) {
            targetType = genericType;
        } else {
            targetType = type;
        }

        return gsonBuilder.create().fromJson(entityReader, targetType);
    }

    public <T> T fromJson(String jsonString, Class<T> type) {
        return gsonBuilder.create().fromJson(jsonString, type);
    }

    public Object fromJson(String jsonString, Type type) {
        return gsonBuilder.create().fromJson(jsonString, type);
    }

    public String toJson(Object object) {
        return gsonBuilder.setPrettyPrinting().create().toJson(object);
    }
}
