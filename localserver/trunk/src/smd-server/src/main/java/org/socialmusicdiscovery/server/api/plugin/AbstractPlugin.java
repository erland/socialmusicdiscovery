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

package org.socialmusicdiscovery.server.api.plugin;

import org.socialmusicdiscovery.server.api.ConfigurationContext;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Abstract plugin helper class which all plugins should inherit from to simplify implementation and avoid the need to implement the plugin
 * methods for which the plugin is satisfied with the default behavior
 */
public abstract class AbstractPlugin implements Plugin {
    /**
     * Configuration context used by the plugin
     */
    protected ConfigurationContext configuration = null;

    /**
     * Default implementation that returns {@link Class#getSimpleName()}}
     * Override this if you want to use another identity for your plugin.
     *
     * @return
     */
    @Override
    public String getId() {
        return getClass().getSimpleName();
    }

    /**
     * Default implementation which returns {@link Plugin#START_PRIORITY_LATE}
     * Override this if you need your plugin to be started early during the server startup
     *
     * @return
     */
    @Override
    public int getStartPriority() {
        return Plugin.START_PRIORITY_LATE;
    }

    /**
     * Default implementation which represents no dependencies to other plugins.
     * Override this if your plugin is dependent on that one or several other plugin has been started before your plugin.
     *
     * @return
     */
    @Override
    public List<String> getDependencies() {
        return new ArrayList<String>();
    }

    /**
     * Default implementation that does nothing, override this if you need to do something when the plugin is stopped
     *
     * @throws PluginException If the plugin failed to stop
     */
    @Override
    public void stop() throws PluginException {
        // Do nothing
    }

    /**
     * Default implementation which can be used by plugins that doesn't have any configuration parameters, override this if the plugin
     * offers configuration parameters
     * @return
     */
    @Override
    public Collection<ConfigurationParameter> getDefaultConfiguration() {
        return new HashSet<ConfigurationParameter>();
    }

    /**
     * Default implementation that just stores the configuration context and makes them accessible through the
     * {@link #getConfiguration} method
     * @param configuration A collection with configuraiton parameters
     */
    @Override
    public void setConfiguration(ConfigurationContext configuration) {
        this.configuration = configuration;
    }

    /**
     * Get configuration context for the plugin, this should be used when reading plugin specific configuration parameters
     * @return The {@link ConfigurationContext} for this plugin
     */
    protected ConfigurationContext getConfiguration() {
        return configuration;
    }
}
