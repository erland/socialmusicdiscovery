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

import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;

import java.util.*;

/**
 * Configuration manager which uses a in-memory representation as its storage
 */
public class MemoryConfigurationManager implements ConfigurationManager {
    /**
     * Storage for the parameters handled
     */
    private Map<String, ConfigurationParameterEntity> parameters = new HashMap<String, ConfigurationParameterEntity>();

    /**
     * Constructs an instance with empty storage
     */
    public MemoryConfigurationManager() {
    }

    /**
     * Constructs an instance and initialize it based on key/value pairs in the provided parameter map
     *
     * @param parameters Parameter key/value pairs that should be used to initalize the configuration manager
     */
    public MemoryConfigurationManager(Map<String, String> parameters) {
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            this.parameters.put(entry.getKey(), new ConfigurationParameterEntity(entry.getKey(), null, entry.getValue()));
        }
    }

    /**
     * Remove any previous configuration parameters with the specified path and replace them with the parameters collection
     * provided as input
     *
     * @param path       Existing parameters starting with this path should be removed
     * @param parameters These parameters should replace the removed parameters
     */
    public void setParametersForPath(String path, Collection<ConfigurationParameter> parameters) {
        Iterator<String> it = this.parameters.keySet().iterator();
        while (it.hasNext() && path != null) {
            String key = it.next();
            if (key.startsWith(path)) {
                it.remove();
            }
        }
        for (ConfigurationParameter parameter : parameters) {
            this.parameters.put(parameter.getId(), new ConfigurationParameterEntity(parameter));
        }
    }

    /**
     * Sets the specified parameter, the in-parameter will be cloned so modifying it afterwards won't affect the parameter
     * handled by this configuration manager.
     *
     * @param parameter Parameter which should be set
     */
    public void setParameter(ConfigurationParameter parameter) {
        this.parameters.put(parameter.getId(), new ConfigurationParameterEntity(parameter));
    }

    /**
     * @inheritDoc
     */
    @Override
    public Set<ConfigurationParameterEntity> getParameters() {
        Set<ConfigurationParameterEntity> resultParameters = new HashSet<ConfigurationParameterEntity>();
        resultParameters.addAll(parameters.values());
        return resultParameters;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Set<ConfigurationParameterEntity> getParametersByPath(String path) {
        Set<ConfigurationParameterEntity> resultParameters = new HashSet<ConfigurationParameterEntity>();
        for (Map.Entry<String, ConfigurationParameterEntity> entry : parameters.entrySet()) {
            if (entry.getKey().startsWith(path)) {
                resultParameters.add(entry.getValue());
            }
        }
        return resultParameters;
    }

    /**
     * @inheritDoc
     */
    @Override
    public ConfigurationParameterEntity getParameter(String id) {
        ConfigurationParameterEntity parameter = parameters.get(id);
        if (parameter != null) {
            return parameter;
        } else {
            return null;
        }
    }
}
