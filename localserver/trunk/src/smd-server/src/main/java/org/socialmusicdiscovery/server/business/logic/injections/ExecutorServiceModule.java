package org.socialmusicdiscovery.server.business.logic.injections;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceModule extends AbstractModule {
    private static ExecutorService mediaImportExecutorService;

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    @Named("mediaimport")
    public ExecutorService provideMediaImportExecutorService() {
        if(mediaImportExecutorService== null) {
            mediaImportExecutorService = Executors.newSingleThreadExecutor();
        }
        return mediaImportExecutorService;
     }
}
