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

package org.socialmusicdiscovery.server.business.logic.injections;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class PropertiesModule extends AbstractModule {
    Properties properties = new Properties();

    @Override
    protected void configure() {
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

    @Provides
    @Named("smd-default-configuration")
    public Properties getDefaultConfiguration() {
        return properties;
    }
}
