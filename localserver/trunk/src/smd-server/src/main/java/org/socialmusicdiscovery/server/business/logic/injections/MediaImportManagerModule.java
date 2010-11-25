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
