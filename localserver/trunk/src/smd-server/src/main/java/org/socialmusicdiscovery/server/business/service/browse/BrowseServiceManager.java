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

package org.socialmusicdiscovery.server.business.service.browse;

import org.socialmusicdiscovery.server.api.ConfigurationContext;
import org.socialmusicdiscovery.server.business.logic.config.MappedConfigurationContext;
import org.socialmusicdiscovery.server.business.logic.config.MergedConfigurationManager;
import org.socialmusicdiscovery.server.business.logic.config.PersistentConfigurationManager;

import java.util.HashMap;
import java.util.Map;

/**
 * The browse service manager is a singleton object which manage all currently active browse services and offers functionality to retrieve
 * a browse service for a specific object type
 */
public class BrowseServiceManager {
    Map<String, Class<? extends BrowseService>> browseServices = new HashMap<String, Class<? extends BrowseService>>();
    Map<String, Class<? extends OnlinePlayableElementService>> playableElementServices = new HashMap<String, Class<? extends OnlinePlayableElementService>>();
    Map<String, ConfigurationContext> configurationContexts = new HashMap<String, ConfigurationContext>();

    /**
     * Register a new browse service for the specified object type
     *
     * @param objectType   Type of object that are returned from this browse service
     * @param serviceClass Browse service class, must have a default constructor as this will be used when creating instances
     */
    public void addBrowseService(String objectType, Class<? extends BrowseService> serviceClass) {
        browseServices.put(objectType, serviceClass);
        configurationContexts.remove(objectType);
        if(OnlinePlayableElementService.class.isAssignableFrom(serviceClass)) {
            playableElementServices.put(objectType, (Class<? extends OnlinePlayableElementService>)serviceClass);
        }
    }

    /**
     * Register a new browse service for the specified object type
     *
     * @param objectType           Type of object that are returned from this browse service
     * @param serviceClass         Browse service class, must have a default constructor as this will be used when creating instances
     * @param configurationContext The configuration context which should be used by this browse service
     */
    public void addBrowseService(String objectType, Class<? extends BrowseService> serviceClass, ConfigurationContext configurationContext) {
        browseServices.put(objectType, serviceClass);
        configurationContexts.put(objectType, configurationContext);
        if(OnlinePlayableElementService.class.isAssignableFrom(serviceClass)) {
            playableElementServices.put(objectType, (Class<? extends OnlinePlayableElementService>)serviceClass);
        }
    }

    /**
     * Unregister a previously registered browse service
     *
     * @param objectType Type type of object that are returned from this browse service
     */
    public void removeBrowseService(String objectType) {
        browseServices.remove(objectType);
        configurationContexts.remove(objectType);
        playableElementServices.remove(objectType);
    }

    /**
     * Get get the browse service for the specified object type
     *
     * @param objectType Object type to get a browse service for
     * @return The browse service or null if it doesn't exist
     */
    public <T extends BrowseService> T getBrowseService(String objectType) {
        try {
            if(browseServices.containsKey(objectType)) {
                T service = (T) browseServices.get(objectType).newInstance();
                ConfigurationContext context = configurationContexts.get(objectType);
                if (context == null) {
                    context = new MappedConfigurationContext(service.getClass().getName() + ".", new MergedConfigurationManager(new PersistentConfigurationManager()));
                }
                service.setConfiguration(context);
                return service;
            }
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Indicates if there is a specific service to get online playable elements for the specified object type
     *
     * @param objectType Object type to get a service for
     * @return true if there is a server, else false
     */
    public Boolean existsOnlinePlayableElementService(String objectType) {
        return playableElementServices.containsKey(objectType);
    }

    /**
     * Get get the service to get playable elements for the specified object type
     *
     * @param objectType Object type to get a service for
     * @return The service or null if it doesn't exist
     */
    public <T extends OnlinePlayableElementService> T getOnlinePlayableElementService(String objectType) {
        try {
            T service = (T) playableElementServices.get(objectType).newInstance();
            ConfigurationContext context = configurationContexts.get(objectType);
            if (context == null) {
                context = new MappedConfigurationContext(service.getClass() + ".", new MergedConfigurationManager(new PersistentConfigurationManager()));
            }
            service.setConfiguration(context);
            return service;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
