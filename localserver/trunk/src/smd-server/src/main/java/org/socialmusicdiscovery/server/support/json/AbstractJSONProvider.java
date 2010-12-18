package org.socialmusicdiscovery.server.support.json;

import com.google.gson.*;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
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

    ;

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
            gsonBuilder.registerTypeAdapter(entry.getKey(), new ImplementationSerializer(entry.getValue()));
            gsonBuilder.registerTypeAdapter(entry.getKey(), new ImplementationCreator(entry.getValue()));
            gsonBuilder.registerTypeAdapter(entry.getKey(), new ImplementationDeserializer(entry.getValue()));
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
}
