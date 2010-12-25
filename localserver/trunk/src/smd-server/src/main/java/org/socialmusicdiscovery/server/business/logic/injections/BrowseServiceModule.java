package org.socialmusicdiscovery.server.business.logic.injections;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.socialmusicdiscovery.server.business.service.browse.*;


public class BrowseServiceModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Named("Release")
    @Singleton
    public BrowseService getReleaseBrowseService() {
        return new ReleaseBrowseService();
    }

    @Provides
    @Named("Track")
    @Singleton
    public BrowseService getTrackBrowseService() {
        return new TrackBrowseService();
    }

    @Provides
    @Named("Work")
    @Singleton
    public BrowseService getWorkBrowseService() {
        return new WorkBrowseService();
    }

    @Provides
    @Named("Artist")
    @Singleton
    public BrowseService getArtistBrowseService() {
        return new ArtistBrowseService();
    }

    @Provides
    @Named("Classification")
    @Singleton
    public BrowseService getClassificationBrowseService() {
        return new ClassificationBrowseService();
    }
}
