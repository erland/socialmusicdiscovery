package org.socialmusicdiscovery.server.business.logic;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.socialmusicdiscovery.server.api.plugin.Plugin;
import org.socialmusicdiscovery.server.api.plugin.PluginException;

import java.util.*;

public class PluginManager {
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
                try {
                    startPlugin(pluginId);
                } catch (PluginException e) {
                    System.err.println("Failed to start: " + pluginId + ": " + e.toString());
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
