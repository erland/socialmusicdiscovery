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
import org.socialmusicdiscovery.server.business.logic.config.MappedConfigurationContext;
import org.socialmusicdiscovery.server.business.logic.config.MemoryConfigurationManager;
import org.socialmusicdiscovery.server.business.logic.config.MergedConfigurationManager;
import org.socialmusicdiscovery.server.business.logic.config.PersistentConfigurationManager;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;

import java.util.*;

/**
 * The plugin manager is a singleton object which manage all currently running plugins and provides functions to start/stop all enabled plugins
 * or an individual specific plugin
 */
public class PluginManager {
    @Inject
    @Named("default-value")
    MemoryConfigurationManager defaultValueConfigurationManager;

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

    /**
     * Constructs a new plugin manager instance, this constructor should never be called directly instead you should create a member variable with
     * an {@link @Inject} annotation which will give you the one and only singleton instance
     * @param plugins The list of plugins which should be managed by this plugin manager instance
     */
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
                ConfigurationParameter defaultValue = defaultValueConfigurationManager.getParameter(pluginConfigurationPath+entity.getId());
                if(defaultValue!=null) {
                    entity.setValue(defaultValue.getValue());
                }else if(System.getProperty(pluginConfigurationPath+entity.getId())!=null) {
                    entity.setValue(System.getProperty(pluginConfigurationPath+entity.getId()));
                }
                entity.setDefaultValue(true);
                defaultConfiguration.add(entity);
            }
            ConfigurationParameter enabledParameter = defaultValueConfigurationManager.getParameter(pluginConfigurationPath+"enabled");
            defaultValueConfigurationManager.setParametersForPath(pluginConfigurationPath, defaultConfiguration);
            if(enabledParameter!=null) {
                enabledParameter.setDefaultValue(true);
                defaultValueConfigurationManager.setParameter(enabledParameter);
            }

            if(defaultValueConfigurationManager.getParameter(pluginConfigurationPath+"enabled")==null && System.getProperty(pluginConfigurationPath+"enabled")!=null) {
                defaultValueConfigurationManager.setParameter(new ConfigurationParameterEntity(pluginConfigurationPath+"enabled", ConfigurationParameter.Type.BOOLEAN, System.getProperty(pluginConfigurationPath+"enabled"), true));
            }

            // Enable plugins by default unless they have specifically requested a specific default state
            if(defaultValueConfigurationManager.getParameter(pluginConfigurationPath+"enabled")==null) {
                defaultValueConfigurationManager.setParameter(new ConfigurationParameterEntity(pluginConfigurationPath+"enabled", ConfigurationParameter.Type.BOOLEAN, "true", true));
            }
            plugin.setConfiguration(new MappedConfigurationContext(pluginConfigurationPath, new MergedConfigurationManager(new PersistentConfigurationManager())));
        }
    }

    /**
     * Start all enabled plugins, the start order will be according to the {@link org.socialmusicdiscovery.server.api.plugin.Plugin#getStartPriority()}
     * value provided by each plugin. If a plugin is dependent on another plugin the other plugin will be started first.
     */
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
                if(new MappedConfigurationContext("",new MergedConfigurationManager(new PersistentConfigurationManager())).getBooleanParameter(PLUGIN_CONFIGURATION_PATH+pluginId+".enabled",true)) {
                    try {
                        startPlugin(pluginId);
                    } catch (PluginException e) {
                        System.err.println("Failed to start: " + pluginId + ": " + e.toString());
                    }
                }
            }
        }
    }

    /**
     * Stop all currently running plugins
     */
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

    /**
     * Start the plugin with the specific identity, if the plugin depends on other plugins through the
     * return value from {@link org.socialmusicdiscovery.server.api.plugin.Plugin#getDependencies()}, the dependencies will automatically be
     * started first.
     * @param pluginId The plugin identity, this is the value from {@link org.socialmusicdiscovery.server.api.plugin.Plugin#getId()}
     * @throws PluginException If the plugin or one of its dependent plugins fails to start
     */
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

    /**
     * Stop the plugin with the specified identity
     * @param pluginId The plugin identity, this is the value from {@link org.socialmusicdiscovery.server.api.plugin.Plugin#getId()}
     * @throws PluginException If the plugin fails to be stopped
     */
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
