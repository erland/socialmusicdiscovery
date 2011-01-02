package org.socialmusicdiscovery.server.api.plugin;

/**
 * Exception that indicates that something went wrong when a plugin was started or stopped
 */
public class PluginException extends Exception {
    /**
     * Constructs an exception with a message that describes the error
     *
     * @param message A message that describes the error
     */
    public PluginException(String message) {
        super(message);
    }

    /**
     * Constructs an exception based on an exception that has been caught by the plugin, use this if you don't have any additional
     * information to provide besides the original exception
     *
     * @param t The original exception that caused the error
     */
    public PluginException(Throwable t) {
        super(t);
    }

    /**
     * Constructs an exception based on an exception that has been caught by the plugin, use this if you can provide extra information
     * about the error besides the original exception, if you don't have any extra information use {@link #PluginException(Throwable)} instead.
     *
     * @param message A message that describes the error
     * @param t       The original exception that caused the error
     */
    public PluginException(String message, Throwable t) {
        super(message, t);
    }
}
