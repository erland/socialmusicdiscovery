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
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;

/**
 * Mapped configuration context which provides simplified access by automatically adding a
 * prefix to all configuration parameters requested
 */
public class MappedConfigurationContext extends AbstractConfigurationContext implements ConfigurationContext {
    String configurationPrefix;
    ConfigurationManager configurationManager;

    /**
     * Creates a new instance with the specified configuration prefix, the prefix will always be used
     * when requesting configuration parameters through this context object
     *
     * @param configurationPrefix
     */
    public MappedConfigurationContext(String configurationPrefix, ConfigurationManager configurationManager) {
        this.configurationPrefix = configurationPrefix;
        this.configurationManager = configurationManager;
    }

    /**
     * Get the string configuration parameter with specified identity
     *
     * @param id           The identity of the configuration parameter, the actual parameter requested will be configurationPrefix+id
     * @param defaultValue The default value to use if parameter doesn't exist
     * @return The value of the configuration parameter
     */
    @Override
    public String getStringParameter(String id, String defaultValue) {
        ConfigurationParameter parameter = configurationManager.getParameter(configurationPrefix + id);
        return parameter != null && parameter.getValue() != null ? parameter.getValue() : defaultValue;
    }
}