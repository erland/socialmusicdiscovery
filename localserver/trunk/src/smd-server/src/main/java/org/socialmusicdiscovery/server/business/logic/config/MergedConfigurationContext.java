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

package org.socialmusicdiscovery.server.business.logic.config;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.socialmusicdiscovery.server.api.ConfigurationContext;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;
import org.socialmusicdiscovery.server.business.repository.config.ConfigurationParameterRepository;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Merged configuration context which takes its configuration primarily from the {@link ConfigurationParameterRepository} but
 * fallback on using the default value {@link ConfigurationManager} if a configuration parameter can't be found in the
 * {@link ConfigurationParameterRepository}
 */
public class MergedConfigurationContext implements ConfigurationContext {
    @Inject
    @Named("default-value")
    ConfigurationManager defaultValuesConfigurationManager;

    @Inject
    ConfigurationParameterRepository configurationParameterRepository;

    public MergedConfigurationContext() {
        InjectHelper.injectMembers(this);
    }

    /**
     * Get the string configuration parameter with specified identity, primarily the parameter value will be taken from
     * {@link ConfigurationParameterRepository} but it will fallback on using the default value handled by {@link ConfigurationManager} of no
     * specific configuration exist
     * @param id The identity of the configuration parameter
     * @return The value of the configuration parameter
     */
    @Override
    public String getStringParameter(String id) {
        ConfigurationParameter parameter = configurationParameterRepository.findById(id);
        if(parameter == null) {
            parameter =  defaultValuesConfigurationManager.getParameter(id);
        }
        return parameter!=null && parameter.getValue()!=null?parameter.getValue():null;
    }

    /**
     * Get the boolean configuration parameter with specified identity, primarily the parameter value will be taken from
     * {@link ConfigurationParameterRepository} but it will fallback on using the default value handled by {@link ConfigurationManager} of no
     * specific configuration exist
     * @param id The identity of the configuration parameter
     * @return The value of the configuration parameter
     */
    @Override
    public Boolean getBooleanParameter(String id) {
        String value = getStringParameter(id);
        return value!=null?Boolean.valueOf(value):null;
    }

    /**
     * Get the integer configuration parameter with specified identity, primarily the parameter value will be taken from
     * {@link ConfigurationParameterRepository} but it will fallback on using the default value handled by {@link ConfigurationManager} of no
     * specific configuration exist
     * @param id The identity of the configuration parameter
     * @return The value of the configuration parameter
     */
    @Override
    public Integer getIntegerParameter(String id) {
        String value = getStringParameter(id);
        return value!=null?Integer.valueOf(value):null;
    }

    /**
     * Get the double configuration parameter with specified identity, primarily the parameter value will be taken from
     * {@link ConfigurationParameterRepository} but it will fallback on using the default value handled by {@link ConfigurationManager} of no
     * specific configuration exist
     * @param id The identity of the configuration parameter
     * @return The value of the configuration parameter
     */
    @Override
    public Double getDoubleParameter(String id) {
        String value = getStringParameter(id);
        return value!=null?Double.valueOf(value):null;
    }

    /**
     * Get the {@link ConfigurationParameterEntity} for the specified identity, this can either be a persistent entity or a in-memory
     * representation of the default configuration. Primarily the parameter will be taken from
     * {@link ConfigurationParameterRepository} but it will fallback on using the in-memory instance handled by {@link ConfigurationManager} of no
     * specific configuration exist
     * @param id The identity of the configuration parameter
     * @return A {@link ConfigurationParameterEntity} instance or null if it doesn't exist
     */
    public ConfigurationParameterEntity getParameter(String id) {
        ConfigurationParameterEntity result = configurationParameterRepository.findById(id);
        if(result == null) {
            result = defaultValuesConfigurationManager.getParameter(id);
        }
        return result;
    }

    /**
     * Get all the {@link ConfigurationParameterEntity} instances managed by this configuration context, this can either be a persistent entities or
     * a in-memory representation of the default configuration. Primarily the parameters will be taken from
     * {@link ConfigurationParameterRepository} but for any parameter which doesn't have a specific configuration it will fallback on using the
     * in-memory instance handled by {@link ConfigurationManager}
     * @return A list of matching {@link ConfigurationParameterEntity} instances
     */
    public Set<ConfigurationParameterEntity> getParameters() {
        Set<ConfigurationParameterEntity> resultParameters = new TreeSet<ConfigurationParameterEntity>(new Comparator<ConfigurationParameterEntity>() {
            @Override
            public int compare(ConfigurationParameterEntity entity1, ConfigurationParameterEntity entity2) {
                return entity1.getId().compareTo(entity2.getId());
            }
        });
        resultParameters.addAll(configurationParameterRepository.findAll());
        resultParameters.addAll(defaultValuesConfigurationManager.getParameters());
        return resultParameters;
    }

    /**
     * Get all the {@link ConfigurationParameterEntity} instances starting with the specified path which is managed by this configuration context,
     * this can either be a persistent entities or a in-memory representation of the default configuration. Primarily the parameters will be taken from
     * {@link ConfigurationParameterRepository} but for any parameter which doesn't have a specific configuration it will fallback on using the
     * in-memory instance handled by {@link ConfigurationManager}. Only the parameters which id starts with the specified path will be returned
     * @return A list of matching {@link ConfigurationParameterEntity} instances
     */
    public Set<ConfigurationParameterEntity> getParametersByPath(String path) {
        Set<ConfigurationParameterEntity> resultParameters = new TreeSet<ConfigurationParameterEntity>(new Comparator<ConfigurationParameterEntity>() {
            @Override
            public int compare(ConfigurationParameterEntity entity1, ConfigurationParameterEntity entity2) {
                return entity1.getId().compareTo(entity2.getId());
            }
        });
        resultParameters.addAll(configurationParameterRepository.findByPath(path));
        resultParameters.addAll(defaultValuesConfigurationManager.getParametersByPath(path));
        return resultParameters;
    }
}