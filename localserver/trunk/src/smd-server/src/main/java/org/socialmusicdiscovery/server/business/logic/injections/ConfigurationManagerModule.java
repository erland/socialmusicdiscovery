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
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.config.ConfigurationManager;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;
import org.socialmusicdiscovery.server.business.repository.config.ConfigurationParameterRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConfigurationManagerModule extends AbstractModule {
    ConfigurationManager configurationManager;

    @Override
    protected void configure() {
    }

    @Inject
    @Provides
    @Singleton
    @Named("default-value")
    public ConfigurationManager provideDefaultValueConfigurationManager(ConfigurationParameterRepository configurationParameterRepository) {
        if(configurationManager==null) {
            configurationManager = new ConfigurationManager();
            Properties defaultProperties = InjectHelper.instanceWithName(Properties.class, "smd-default-configuration");
            if(defaultProperties!=null) {
                List<ConfigurationParameter> parameters = new ArrayList<ConfigurationParameter>();
                for (Map.Entry<Object, Object> entry : defaultProperties.entrySet()) {
                    String property = entry.getKey().toString();
                    String value = entry.getValue().toString();
                    if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                        parameters.add(new ConfigurationParameterEntity(property, ConfigurationParameter.Type.BOOLEAN, value));
                    }else {
                        try {
                            if(value.contains(".")) {
                                Double.parseDouble(value);
                                parameters.add(new ConfigurationParameterEntity(property, ConfigurationParameter.Type.DOUBLE, value));
                            }else {
                                Integer.parseInt(value);
                                parameters.add(new ConfigurationParameterEntity(property, ConfigurationParameter.Type.INTEGER, value));
                            }
                        }catch (NumberFormatException e) {
                            parameters.add(new ConfigurationParameterEntity(property, ConfigurationParameter.Type.STRING, value));
                        }
                    }
                }
                configurationManager.setParametersForPath("",parameters);
            }
        }
        return configurationManager;
     }
}
