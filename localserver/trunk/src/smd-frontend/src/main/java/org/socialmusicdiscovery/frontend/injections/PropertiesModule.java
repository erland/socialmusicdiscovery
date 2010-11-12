package org.socialmusicdiscovery.frontend.injections;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesModule extends AbstractModule {
    private static Map<String,String> commandLineAttributes = new HashMap<String,String>();

    public static void init(org.apache.pivot.collections.Map<String, String> commandLineAttributes) {
        for (String attribute : commandLineAttributes) {
            PropertiesModule.commandLineAttributes.put(attribute,commandLineAttributes.get(attribute));
        }
    }

    @Override
    protected void configure() {
        Properties properties = new Properties();
        try {
            InputStream defaultFile = getClass().getResourceAsStream("/socialmusicdiscovery-default.properties");
            properties.load(defaultFile);
            InputStream file = null;
            try {
                file = new FileInputStream("socialmusicdiscovery-default.properties");
                properties.load(file);
            } catch (FileNotFoundException e) {
                // Do nothing
            }
            file = getClass().getResourceAsStream("/socialmusicdiscovery-default.properties");
            if(file != null) {
                properties.load(file);
            }

            for (Map.Entry<String, String> entry : commandLineAttributes.entrySet()) {
                properties.setProperty(entry.getKey(), entry.getValue());
            }

            Enumeration propertyNames= properties.propertyNames();
            while (propertyNames.hasMoreElements()) {
                String property = (String) propertyNames.nextElement();
                bind(String.class).annotatedWith(Names.named(property)).toInstance(properties.getProperty(property));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
