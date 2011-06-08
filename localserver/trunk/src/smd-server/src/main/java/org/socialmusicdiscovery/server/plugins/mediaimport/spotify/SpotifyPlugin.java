package org.socialmusicdiscovery.server.plugins.mediaimport.spotify;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.api.plugin.AbstractPlugin;
import org.socialmusicdiscovery.server.api.plugin.PluginException;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;
import org.socialmusicdiscovery.server.business.service.browse.BrowseMenuManager;
import org.socialmusicdiscovery.server.business.service.browse.BrowseServiceManager;
import org.socialmusicdiscovery.server.business.service.browse.Menu;
import org.socialmusicdiscovery.server.business.service.browse.MenuLevel;

import java.util.Arrays;
import java.util.Collection;

/**
 * Spotify plugin that provides context menus for artists, releases and tracks which can be used to
 * find the matching Spotify artist, release, track
 */
public class SpotifyPlugin extends AbstractPlugin {
    @Inject
    BrowseServiceManager browseServiceManager;

    @Inject
    BrowseMenuManager browseMenuManager;

    @Override
    public Collection<ConfigurationParameter> getDefaultConfiguration() {
        return Arrays.asList(
                (ConfigurationParameter) new ConfigurationParameterEntity("location", ConfigurationParameter.Type.STRING, "SE")
        );
    }

    @Override
    public boolean start() throws PluginException {
        InjectHelper.injectMembers(this);

        browseMenuManager.addDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, SpotifyArtist.class.getSimpleName(), "%object.name");
        browseMenuManager.addDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, SpotifyAlbum.class.getSimpleName(), "%object.name");
        browseMenuManager.addDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, SpotifyTrack.class.getSimpleName(), "%object.number||[%object.number,. ]||%object.name");

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.CONTEXT,
                new Menu("Artist", "spotifyartists", "On Spotify", Menu.BOTTOM_WEIGHT,
                        Arrays.asList(
                                new MenuLevel(SpotifyArtist.class.getSimpleName(),
                                        false),
                                new MenuLevel(SpotifyAlbum.class.getSimpleName(),
                                        false,
                                        1L),
                                new MenuLevel(SpotifyTrack.class.getSimpleName(),
                                        false,
                                        2L)
                        )));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.CONTEXT,
                new Menu("Release", "spotifyalbums", "On Spotify", Menu.BOTTOM_WEIGHT,
                        Arrays.asList(
                                new MenuLevel(SpotifyAlbum.class.getSimpleName(),
                                        false),
                                new MenuLevel(SpotifyTrack.class.getSimpleName(),
                                        false,
                                        1L)
                        )));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.CONTEXT,
                new Menu("Track", "spotifytracks", "On Spotify", Menu.BOTTOM_WEIGHT,
                        Arrays.asList(
                                new MenuLevel(SpotifyTrack.class.getSimpleName(),
                                        browseMenuManager.getDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, SpotifyTrack.class.getSimpleName()),
                                        false)
                        )));

        browseServiceManager.addBrowseService(SpotifyArtist.class.getSimpleName(), SpotifyArtistBrowseService.class);
        browseServiceManager.addBrowseService(SpotifyAlbum.class.getSimpleName(), SpotifyAlbumBrowseService.class);
        browseServiceManager.addBrowseService(SpotifyTrack.class.getSimpleName(), SpotifyTrackBrowseService.class);
        return true;
    }

    @Override
    public void stop() throws PluginException {
        browseMenuManager.removeMenu(BrowseMenuManager.MenuType.CONTEXT, "Artist", "spotifyartists");
        browseMenuManager.removeMenu(BrowseMenuManager.MenuType.CONTEXT, "Release", "spotifyalbums");
        browseMenuManager.removeMenu(BrowseMenuManager.MenuType.CONTEXT, "Track", "spotifytracks");

        browseMenuManager.removeDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, SpotifyArtist.class.getSimpleName());
        browseMenuManager.removeDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, SpotifyAlbum.class.getSimpleName());
        browseMenuManager.removeDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, SpotifyTrack.class.getSimpleName());

        browseServiceManager.removeBrowseService(SpotifyArtist.class.getSimpleName());
        browseServiceManager.removeBrowseService(SpotifyAlbum.class.getSimpleName());
        browseServiceManager.removeBrowseService(SpotifyTrack.class.getSimpleName());
        super.stop();
    }
}
