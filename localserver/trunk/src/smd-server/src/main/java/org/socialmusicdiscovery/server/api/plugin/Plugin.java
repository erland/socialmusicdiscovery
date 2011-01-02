package org.socialmusicdiscovery.server.api.plugin;

import java.util.List;

public interface Plugin {
    /**
     * Should return a unique identity for this plugin
     */
    String getId();

    /**
     * Start priority for plugins which other plugins usually are dependent on
     */
    static int START_PRIORITY_EARLY = 10;
    /**
     * Start priority for plugins which doesn't need to start early or is dependent on a lot of other plugins
     */
    static int START_PRIORITY_LATE = 90;

    /**
     * Should return a number indicating the startup priority if multiple plugins are started at the same time.
     * Use the {@link #START_PRIORITY_EARLY} and {@link #START_PRIORITY_LATE} as a guidance.
     *
     * @return
     */
    int getStartPriority();

    /**
     * Should return a list of plugin identifiers of plugins which this plugin is dependent on.
     */
    List<String> getDependencies();

    /**
     * Will be called when the plugin should be activated, if you want to do any initialization you should do it inside this {@link #start} method
     *
     * @return true if the plugin is going to continue to run after the method returns, false if the plugin has already done its work after the method returns and can be considered to be stopped..
     * @throws PluginException If an error occurs that results in that the plugin failed to start
     */
    boolean start() throws PluginException;

    /**
     * Will be called when the plugin should be deactivated, if you want to do any clean up you should do it inside this {@link #stop} method
     *
     * @throws PluginException If an error occurs that results in that the plugin couldn't be stopped
     */
    void stop() throws PluginException;
}
