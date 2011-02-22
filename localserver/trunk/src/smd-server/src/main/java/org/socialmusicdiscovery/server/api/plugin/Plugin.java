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
