package org.socialmusicdiscovery.server.business.logic.injections;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class PropertiesModule extends AbstractModule {
    @Override
    protected void configure() {
        Properties properties = new Properties();
        try {
            InputStream defaultFile = getClass().getResourceAsStream("/socialmusicdiscovery-default.properties");
            properties.load(defaultFile);
            InputStream file = null;
            try {
                file = new FileInputStream("socialmusicdiscovery.properties");
                properties.load(file);
            } catch (FileNotFoundException e) {
                // Do nothing
            }
            file = getClass().getResourceAsStream("/socialmusicdiscovery.properties");
            if(file != null) {
                properties.load(file);
            }

            Enumeration propertyNames= properties.propertyNames();
            while (propertyNames.hasMoreElements()) {
                String property = (String) propertyNames.nextElement();
                if(System.getProperty(property) == null) {
                    System.setProperty(property,properties.get(property).toString());
                }
                bind(String.class).annotatedWith(Names.named(property)).toInstance(System.getProperty(property));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
