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
import org.socialmusicdiscovery.server.api.mediaimport.MediaImporter;
import org.socialmusicdiscovery.server.api.mediaimport.PostProcessor;
import org.socialmusicdiscovery.server.business.logic.MediaImportManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

public class MediaImportManagerModule extends AbstractModule {
    private static MediaImportManager mediaImportManager;

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public MediaImportManager provideMediaImportManager() {
        if(mediaImportManager== null) {
            Map<String, MediaImporter> mediaImporters = new HashMap<String,MediaImporter>();
            ServiceLoader<MediaImporter> mediaImporterLoader = ServiceLoader.load(MediaImporter.class);
            Iterator<MediaImporter> mediaImporterIterator = mediaImporterLoader.iterator();
            while(mediaImporterIterator.hasNext()) {
                MediaImporter importer = mediaImporterIterator.next();
                mediaImporters.put(importer.getId(),importer);
            }

            Map<String, PostProcessor> postProcessors = new HashMap<String,PostProcessor>();
            ServiceLoader<PostProcessor> postProcessorLoader = ServiceLoader.load(PostProcessor.class);
            Iterator<PostProcessor> postProcessorIterator = postProcessorLoader.iterator();
            while(postProcessorIterator.hasNext()) {
                PostProcessor postProcessor = postProcessorIterator.next();
                postProcessors.put(postProcessor.getId(),postProcessor);
            }
            mediaImportManager = new MediaImportManager(mediaImporters, postProcessors);
        }
        return mediaImportManager;
     }
}
