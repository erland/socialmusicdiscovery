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

package org.socialmusicdiscovery.server.plugins.json;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import org.socialmusicdiscovery.server.api.plugin.AbstractPlugin;
import org.socialmusicdiscovery.server.api.plugin.PluginException;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class JSONPlugin extends AbstractPlugin {
    @Inject
    @Named("org.socialmusicdiscovery.server.port")
    String serverPort;

    /**
     * Web server thread
     */
    private SelectorThread threadSelector = null;

    @Override
    public boolean start() throws PluginException {
        if (serverPort == null) {
            InjectHelper.injectMembers(this);
        }
        Map<String, String> initParams = new HashMap<String, String>();
        initParams.put("com.sun.jersey.config.property.packages", "org.socialmusicdiscovery.server.api;org.socialmusicdiscovery.server.business.logic.jersey");
        //initParams.put("com.sun.jersey.config.property.WadlGeneratorConfig", "org.socialmusicdiscovery.server.business.logic.jersey.SMDWadlGeneratorConfig");

        System.out.println("Starting grizzly...");
        URI uri = UriBuilder.fromUri("http://localhost/").port(Integer.parseInt(serverPort)).build();
        try {
            threadSelector = GrizzlyWebContainerFactory.create(uri, initParams);
        } catch (IOException e) {
            throw new PluginException(e);
        }
        System.out.println(String.format("Try out %sapplication.wadl", uri));
        return true;
    }

    @Override
    public void stop() throws PluginException {
        threadSelector.stopEndpoint();
    }
}
