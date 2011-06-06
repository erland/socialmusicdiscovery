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

package org.socialmusicdiscovery.server.business.logic;

import org.socialmusicdiscovery.server.api.mediaimport.ImageProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * The image provider manager is a singleton object which manage all currently active image providers and offers functionality to retrieve
 * a image provider for a specific image source
 */
public class ImageProviderManager {
    Map<String,ImageProvider> imageProviders;

    public ImageProviderManager(Map<String,ImageProvider> imageProviders) {
        this.imageProviders = imageProviders;
    }

    /**
     * Get image provider for the specified image source
     * @param source Identity of the image source to get a provider for
     * @return The image provider or null if no provider exists for this source
     */
    public ImageProvider getProvider(String source) {
        return imageProviders.get(source);
    }

    /**
     * Get image URL for the specified image source and uri
     * @param source Identity of the image source to get a URL for
     * @param uri URI fo the image to get
     * @return The image provider or null if no provider exists for this source
     */
    public URL getImageURL(String source, String uri) {
        try {
            return new URL(imageProviders.get(source).getImageURL(uri));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get image URL for the specified image source and uri and rescale it to the specified size
     * @param source Identity of the image source to get a URL for
     * @param uri URI fo the image to get
     * @param maxWidth Maximum width of image
     * @param maxHeight Maximum height of image
     * @return The URL of the image or null if no provider exists for this source
     */
    public URL getImageURL(String source, String uri, Integer maxWidth, Integer maxHeight) {
        try {
            return new URL(imageProviders.get(source).getImageURL(uri, maxWidth, maxHeight));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
