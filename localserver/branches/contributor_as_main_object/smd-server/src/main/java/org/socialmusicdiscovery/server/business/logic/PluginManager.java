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

package org.socialmusicdiscovery.server.business.logic;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.socialmusicdiscovery.server.api.plugin.Plugin;
import org.socialmusicdiscovery.server.api.plugin.PluginException;
import org.socialmusicdiscovery.server.business.logic.config.ConfigurationManager;
import org.socialmusicdiscovery.server.business.logic.config.MappedConfigurationContext;
import org.socialmusicdiscovery.server.business.logic.config.MergedConfigurationContext;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;

import java.util.*;

public class PluginManager {
    @Inject
    @Named("default-value")
    ConfigurationManager defaultValueConfigurationManager;

    /**
     * The configuration path where plugin configurations are stored
     */
    private static final String PLUGIN_CONFIGURATION_PATH = "org.socialmusicdiscovery.server.plugins.";

    /**
     * Contains all available plugin modules
     */
    Map<String, Plugin> plugins;

    /**
     * Contains all currently activated plugin modules
     */
    Map<String, Plugin> runningPlugins = new HashMap<String, Plugin>();

    public PluginManager(Map<String, Plugin> plugins) {
        this.plugins = plugins;
        InjectHelper.injectMembers(this);
        for (Plugin plugin : plugins.values()) {
            Collection<ConfigurationParameter> defaultPluginConfiguration = plugin.getDefaultConfiguration();
            String pluginConfigurationPath = PLUGIN_CONFIGURATION_PATH+plugin.getId()+".";

            Set<ConfigurationParameter> defaultConfiguration = new HashSet<ConfigurationParameter>();
            for (ConfigurationParameter parameter : defaultPluginConfiguration) {
                ConfigurationParameterEntity entity = new ConfigurationParameterEntity(parameter);
                if(!entity.getId().startsWith(pluginConfigurationPath)) {
                    entity.setId(pluginConfigurationPath+entity.getId());
                }
                entity.setDefaultValue(true);
                defaultConfiguration.add(entity);
            }
            defaultValueConfigurationManager.setParametersForPath(pluginConfigurationPath, defaultConfiguration);

            // Enable plugins by default unless they have specifically requested a specific default state
            if(defaultValueConfigurationManager.getParameter(pluginConfigurationPath+"enabled")==null) {
                defaultValueConfigurationManager.setParameter(new ConfigurationParameterEntity(pluginConfigurationPath+"enabled", ConfigurationParameter.Type.BOOLEAN, "true", true));
            }
            plugin.setConfiguration(new MappedConfigurationContext(pluginConfigurationPath));
        }
    }

    public void startAll() {
        List<String> pluginIdentities = new ArrayList<String>(plugins.keySet());
        Collections.sort(pluginIdentities, new Comparator<String>() {
            @Override
            public int compare(String p1, String p2) {
                Plugin plugin1 = plugins.get(p1);
                Plugin plugin2 = plugins.get(p2);
                return new CompareToBuilder().append(plugin1.getStartPriority(), plugin2.getStartPriority()).toComparison();
            }
        });

        for (String pluginId : pluginIdentities) {
            if (!runningPlugins.containsKey(pluginId)) {
                if(new MergedConfigurationContext().getBooleanParameter(PLUGIN_CONFIGURATION_PATH+pluginId+".enabled")) {
                    try {
                        startPlugin(pluginId);
                    } catch (PluginException e) {
                        System.err.println("Failed to start: " + pluginId + ": " + e.toString());
                    }
                }
            }
        }
    }

    public void stopAll() {
        for (String pluginId : plugins.keySet()) {
            if (runningPlugins.containsKey(pluginId)) {
                try {
                    stopPlugin(pluginId);
                } catch (PluginException e) {
                    System.err.println("Failed to stop: " + pluginId + ": " + e.toString());
                }
            }
        }
    }

    public void startPlugin(String pluginId) throws PluginException {
        if (!runningPlugins.containsKey(pluginId)) {
            Plugin plugin = plugins.get(pluginId);
            if (plugin != null) {
                List<String> dependencies = plugin.getDependencies();
                for (String dependency : dependencies) {
                    if (!runningPlugins.containsKey(dependency)) {
                        startPlugin(dependency);
                    }
                }
                System.out.println("Starting " + pluginId + ": " + plugin.getClass().getName());
                System.out.flush();
                if (plugin.start()) {
                    runningPlugins.put(pluginId, plugin);
                }
                System.out.println("Started " + pluginId + ": OK");
            }
        }
    }

    public void stopPlugin(String pluginId) throws PluginException {
        Plugin plugin = runningPlugins.get(pluginId);
        if (plugin != null) {
            System.out.println("Stopping " + pluginId + ": " + plugin.getClass().getName());
            plugin.stop();
            runningPlugins.remove(pluginId);
            System.out.println("Stopped " + pluginId + ": OK");
        }
    }
}
