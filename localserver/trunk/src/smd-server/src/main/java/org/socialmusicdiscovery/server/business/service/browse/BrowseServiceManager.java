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

import java.util.HashMap;
import java.util.Map;

/**
 * The browse service manager is a singleton object which manage all currently active browse services and offers functionality to retrieve
 * a browse service for a specific object type
 */
public class BrowseServiceManager {
    Map<String,Class<? extends BrowseService>> browseServices = new HashMap<String,Class<? extends BrowseService>>();

    /**
     * Register a new browse service for the specified object type
     * @param objectType Type of object that are returned from this browse service
     * @param serviceClass Browse service class, must have a default constructor as this will be used when creating instances
     */
    public void addBrowseService(String objectType, Class<? extends BrowseService> serviceClass) {
        browseServices.put(objectType, serviceClass);
    }

    /**
     * Unregister a previously registered browse service
     * @param objectType Type type of object that are returned from this browse service
     */
    public void removeBrowseService(String objectType) {
        browseServices.remove(objectType);
    }

    /**
     * Get get the browse service for the specified object type
     * @param objectType Object type to get a browse service for
     * @return The browse service or null if it doesn't exist
     */
    public <T extends BrowseService> T getBrowseService(String objectType) {
        try {
            return (T)browseServices.get(objectType).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

}
