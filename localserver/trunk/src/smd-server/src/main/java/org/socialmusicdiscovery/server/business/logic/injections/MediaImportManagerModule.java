package org.socialmusicdiscovery.server.business.logic.injections;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.socialmusicdiscovery.server.api.mediaimport.MediaImporter;
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
            ServiceLoader<MediaImporter> loader = ServiceLoader.load(MediaImporter.class);
            Iterator<MediaImporter> iterator = loader.iterator();
            while(iterator.hasNext()) {
                MediaImporter importer = iterator.next();
                mediaImporters.put(importer.getId(),importer);
            }
            mediaImportManager = new MediaImportManager(mediaImporters);
        }
        return mediaImportManager;
     }
}
