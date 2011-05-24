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

import org.socialmusicdiscovery.server.api.ConfigurationContext;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;

import java.util.Set;

/**
 * Mapped configuration context which provides simplified access by automatically adding a
 * prefix to all configuration parameters requested
 */
public class MappedConfigurationContext implements ConfigurationContext {
    String configurationPrefix;

    /**
     * Creates a new instance with the specified configuration prefix, the prefix will always be used
     * when requesting configuration parameters through this context object
     * @param configurationPrefix
     */
    public MappedConfigurationContext(String configurationPrefix) {
        this.configurationPrefix = configurationPrefix;
    }

    /**
     * Get the string configuration parameter with specified identity
     * @param id The identity of the configuration parameter, the actual parameter requested will be configurationPrefix+id
     * @return The value of the configuration parameter
     */
    @Override
    public String getStringParameter(String id) {
        return new MergedConfigurationContext().getStringParameter(configurationPrefix +id);
    }

    /**
     * Get the boolean configuration parameter with specified identity
     * @param id The identity of the configuration parameter, the actual parameter requested will be configurationPrefix+id
     * @return The value of the configuration parameter
     */
    @Override
    public Boolean getBooleanParameter(String id) {
        return new MergedConfigurationContext().getBooleanParameter(configurationPrefix + id);
    }

    /**
     * Get the integer configuration parameter with specified identity
     * @param id The identity of the configuration parameter, the actual parameter requested will be configurationPrefix+id
     * @return The value of the configuration parameter
     */
    @Override
    public Integer getIntegerParameter(String id) {
        return new MergedConfigurationContext().getIntegerParameter(configurationPrefix + id);
    }

    /**
     * Get the double configuration parameter with specified identity
     * @param id The identity of the configuration parameter, the actual parameter requested will be configurationPrefix+id
     * @return The value of the configuration parameter
     */
    @Override
    public Double getDoubleParameter(String id) {
        return new MergedConfigurationContext().getDoubleParameter(configurationPrefix + id);
    }

    /**
     * Get the {@link ConfigurationParameterEntity} for the specified identity, this can either be a persistent entity or a in-memory
     * representation of the default configuration
     * @param id The identity of the configuration parameter, the actual parameter requested will be configurationPrefix+id
     * @return A {@link ConfigurationParameterEntity} instance or null if it doesn't exist
     */
    public ConfigurationParameterEntity getParameter(String id) {
        return new MergedConfigurationContext().getParameter(id);
    }

    /**
     * Get all the {@link ConfigurationParameterEntity} instances managed by this configuration context, this can either be a persistent entities or
     * a in-memory representation of the default configuration. Only the parameters which id starts with the configurationPrefix will be returned
     * @return A list of matching {@link ConfigurationParameterEntity} instances
     */
    public Set<ConfigurationParameterEntity> getParameters() {
        return new MergedConfigurationContext().getParameters();
    }

    /**
     * Get all the {@link ConfigurationParameterEntity} instances starting with the specified path which is managed by this configuration context,
     * this can either be a persistent entities or a in-memory representation of the default configuration. Only the parameters which id starts with
     * the configurationPrefix+path will be returned
     * @return A list of matching {@link ConfigurationParameterEntity} instances
     */
    public Set<ConfigurationParameterEntity> getParametersByPath(String path) {
        return new MergedConfigurationContext().getParametersByPath(configurationPrefix + path);
    }
}