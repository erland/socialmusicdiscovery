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

import org.socialmusicdiscovery.server.api.mediaimport.AbstractImageProvider;
import org.socialmusicdiscovery.server.business.model.core.Image;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Image provider that provides images based on a full URL which is public and available on internet
 */
public class InternetImageProvider extends AbstractImageProvider {
    public static final String PROVIDER_ID = "internet";

    public InternetImageProvider() {
        super(PROVIDER_ID);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getImageURL(Image image) {
        return image.getUri();
        // TODO: Do we want all images to go through mysqueezebox.com image proxy ?
        //return "http://www.mysqueezebox.com/public/imageproxy?u=" + image.getUri();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getImageURL(Image image, Integer maxWidth, Integer maxHeight) {
        try {
            return "http://www.mysqueezebox.com/public/imageproxy?u=" + URLEncoder.encode(image.getUri(),"utf8") + "&w=" + maxWidth + "&h=" + maxHeight;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
