package org.socialmusicdiscovery.server.api.plugin;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPlugin implements Plugin {
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
}
