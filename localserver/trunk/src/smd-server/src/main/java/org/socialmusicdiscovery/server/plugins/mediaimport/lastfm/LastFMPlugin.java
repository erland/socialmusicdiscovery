package org.socialmusicdiscovery.server.plugins.mediaimport.lastfm;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.api.plugin.AbstractPlugin;
import org.socialmusicdiscovery.server.api.plugin.PluginException;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.service.browse.BrowseMenuManager;
import org.socialmusicdiscovery.server.business.service.browse.BrowseServiceManager;
import org.socialmusicdiscovery.server.business.service.browse.Menu;
import org.socialmusicdiscovery.server.business.service.browse.MenuLevel;

import java.util.Arrays;

/**
 * LastFM plugin that provides context menus for artists, releases and tracks which can be used to
 * find the matching LastFM artist, release, track
 */
public class LastFMPlugin extends AbstractPlugin {
    @Inject
    BrowseServiceManager browseServiceManager;

    @Inject
    BrowseMenuManager browseMenuManager;

    @Override
    public boolean start() throws PluginException {
        InjectHelper.injectMembers(this);

        browseMenuManager.addDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, LastFMArtist.class.getSimpleName(), "%object.name");
        browseMenuManager.addDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, LastFMAlbum.class.getSimpleName(), "%object.name");
        browseMenuManager.addDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, LastFMTrack.class.getSimpleName(), "%object.number||[%object.number,. ]||%object.name");

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.CONTEXT,
                new Menu("Artist", "lastfmartists", "On LastFM", Menu.BOTTOM_WEIGHT,
                        Arrays.asList(
                                new MenuLevel(LastFMArtist.class.getSimpleName(),
                                        false),
                                new MenuLevel(LastFMAlbum.class.getSimpleName(),
                                        false,
                                        1L),
                                new MenuLevel(LastFMTrack.class.getSimpleName(),
                                        false,
                                        2L)
                        )));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.CONTEXT,
                new Menu("Release", "lastfmalbums", "On LastFM", Menu.BOTTOM_WEIGHT,
                        Arrays.asList(
                                new MenuLevel(LastFMAlbum.class.getSimpleName(),
                                        false),
                                new MenuLevel(LastFMTrack.class.getSimpleName(),
                                        false,
                                        1L)
                        )));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.CONTEXT,
                new Menu("Track", "lastfmtracks", "On LastFM", Menu.BOTTOM_WEIGHT,
                        Arrays.asList(
                                new MenuLevel(LastFMTrack.class.getSimpleName(),
                                        false)
                        )));

        browseServiceManager.addBrowseService(LastFMArtist.class.getSimpleName(), LastFMArtistBrowseService.class);
        browseServiceManager.addBrowseService(LastFMAlbum.class.getSimpleName(), LastFMAlbumBrowseService.class);
        browseServiceManager.addBrowseService(LastFMTrack.class.getSimpleName(), LastFMTrackBrowseService.class);
        return true;
    }

    @Override
    public void stop() throws PluginException {
        browseMenuManager.removeMenu(BrowseMenuManager.MenuType.CONTEXT, "Artist", "lastfmartists");
        browseMenuManager.removeMenu(BrowseMenuManager.MenuType.CONTEXT, "Release", "lastfmalbums");
        browseMenuManager.removeMenu(BrowseMenuManager.MenuType.CONTEXT, "Track", "lastfmtracks");

        browseMenuManager.removeDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, LastFMArtist.class.getSimpleName());
        browseMenuManager.removeDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, LastFMAlbum.class.getSimpleName());
        browseMenuManager.removeDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, LastFMTrack.class.getSimpleName());

        browseServiceManager.removeBrowseService(LastFMArtist.class.getSimpleName());
        browseServiceManager.removeBrowseService(LastFMAlbum.class.getSimpleName());
        browseServiceManager.removeBrowseService(LastFMTrack.class.getSimpleName());
        super.stop();
    }
}
