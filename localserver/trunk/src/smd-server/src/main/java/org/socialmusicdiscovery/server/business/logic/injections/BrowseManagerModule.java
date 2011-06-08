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

package org.socialmusicdiscovery.server.business.logic.injections;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.socialmusicdiscovery.server.business.service.browse.BrowseMenuManager;
import org.socialmusicdiscovery.server.business.service.browse.BrowseService;
import org.socialmusicdiscovery.server.business.service.browse.BrowseServiceManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Provides the singleton instance of the {@link BrowseServiceManager} which manage all services in the application.
 * The browse services are found using {@link ServiceLoader} which looks up all registered implementations of the {@link BrowseService} interface.
 */
public class BrowseManagerModule extends AbstractModule {
    private static BrowseServiceManager serviceManager;
    private static BrowseMenuManager menuManager;

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public BrowseServiceManager provideServiceManager() {
        if (serviceManager == null) {
            serviceManager = new BrowseServiceManager();

            // Load all plugins registered in:
            // /META-INF/services/org.socialmusicdiscovery.server.api.mediaimport.ImageProvider
            Map<String, BrowseService> browseServices = new HashMap<String, BrowseService>();
            ServiceLoader<BrowseService> pluginLoader = ServiceLoader.load(BrowseService.class);
            Iterator<BrowseService> it = pluginLoader.iterator();
            while (it.hasNext()) {
                BrowseService browseService = it.next();
                serviceManager.addBrowseService(browseService.getObjectType(), browseService.getClass());
            }

        }
        return serviceManager;
    }

    @Provides
    @Singleton
    public BrowseMenuManager provideMenuManager() {
        if (menuManager == null) {
            menuManager = new BrowseMenuManager();
        }
        return menuManager;
    }
}
