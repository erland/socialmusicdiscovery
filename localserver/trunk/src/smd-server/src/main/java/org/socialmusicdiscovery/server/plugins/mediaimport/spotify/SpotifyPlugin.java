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

package org.socialmusicdiscovery.server.plugins.mediaimport.spotify;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.api.plugin.AbstractPlugin;
import org.socialmusicdiscovery.server.api.plugin.PluginException;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;
import org.socialmusicdiscovery.server.business.service.browse.*;

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
                            new MenuLevelFolder("Artist","spotifyartists","On Spotify", MenuLevel.BOTTOM_WEIGHT,
                                new MenuLevelDynamic(SpotifyArtist.class.getSimpleName(),
                                        null,
                                        false,
                                    new MenuLevelDynamic(SpotifyAlbum.class.getSimpleName(),
                                            null,
                                            true,
                                            1L,
                                        new MenuLevelDynamic(SpotifyTrack.class.getSimpleName(),
                                                null,
                                                true,
                                                2L)))));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.CONTEXT,
                            new MenuLevelFolder("Release","spotifyalbums","On Spotify", MenuLevel.BOTTOM_WEIGHT,
                                new MenuLevelDynamic(SpotifyAlbum.class.getSimpleName(),
                                        null,
                                        true,
                                    new MenuLevelDynamic(SpotifyTrack.class.getSimpleName(),
                                            null,
                                            true,
                                            1L))));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.CONTEXT,
                            new MenuLevelFolder(SpotifyArtist.class.getSimpleName(), "spotifyalbums","Albums", MenuLevel.MIDDLE_WEIGHT,
                                new MenuLevelDynamic(SpotifyAlbum.class.getSimpleName(),
                                        null,
                                        true,
                                    new MenuLevelDynamic(SpotifyTrack.class.getSimpleName(),
                                            null,
                                            true,
                                            1L))));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.CONTEXT,
                            new MenuLevelFolder("Track","spotifytracks","On Spotify", MenuLevel.BOTTOM_WEIGHT,
                                new MenuLevelDynamic(SpotifyTrack.class.getSimpleName(),
                                        null,
                                        true)));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.CONTEXT,
                            new MenuLevelFolder(SpotifyAlbum.class.getSimpleName(), "spotifytracks","Tracks", MenuLevel.MIDDLE_WEIGHT,
                                new MenuLevelDynamic(SpotifyTrack.class.getSimpleName(),
                                        null,
                                        true)));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.CONTEXT,
                            new MenuLevelCommand(SpotifyArtist.class.getSimpleName(), "spotifyimportartist", "Import From Spotify"));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.CONTEXT,
                            new MenuLevelCommand(SpotifyAlbum.class.getSimpleName(), "spotifyimportalbum", "Import from Spotify"));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.CONTEXT,
                            new MenuLevelCommand(SpotifyTrack.class.getSimpleName(), "spotifyimporttrack", "Import from Spotify"));

        browseMenuManager.addCommand("spotifyimportartist", SpotifyImport.class);
        browseMenuManager.addCommand("spotifyimportalbum", SpotifyImport.class);
        browseMenuManager.addCommand("spotifyimporttrack", SpotifyImport.class);

        browseServiceManager.addBrowseService(SpotifyArtist.class.getSimpleName(), SpotifyArtistBrowseService.class, getConfiguration());
        browseServiceManager.addBrowseService(SpotifyAlbum.class.getSimpleName(), SpotifyAlbumBrowseService.class, getConfiguration());
        browseServiceManager.addBrowseService(SpotifyTrack.class.getSimpleName(), SpotifyTrackBrowseService.class, getConfiguration());
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
