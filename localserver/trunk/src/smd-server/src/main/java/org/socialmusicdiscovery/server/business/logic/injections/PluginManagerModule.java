package org.socialmusicdiscovery.server.business.logic.injections;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.socialmusicdiscovery.server.api.plugin.Plugin;
import org.socialmusicdiscovery.server.business.logic.PluginManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

public class PluginManagerModule extends AbstractModule {
    private static PluginManager pluginManager;

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public PluginManager providePluginManager() {
        if (pluginManager == null) {
            Map<String, Plugin> plugins = new HashMap<String, Plugin>();
            ServiceLoader<Plugin> pluginLoader = ServiceLoader.load(Plugin.class);
            Iterator<Plugin> pluginIterator = pluginLoader.iterator();
            while (pluginIterator.hasNext()) {
                Plugin plugin = pluginIterator.next();
                plugins.put(plugin.getId(), plugin);
            }

            pluginManager = new PluginManager(plugins);
        }
        return pluginManager;
    }
}
