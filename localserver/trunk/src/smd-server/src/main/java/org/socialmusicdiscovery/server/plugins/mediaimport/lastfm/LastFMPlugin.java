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

package org.socialmusicdiscovery.server.plugins.mediaimport.lastfm;

import com.google.inject.Inject;
import nu.xom.ParsingException;
import org.socialmusicdiscovery.server.api.plugin.AbstractPlugin;
import org.socialmusicdiscovery.server.api.plugin.PluginException;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.service.browse.BrowseMenuManager;
import org.socialmusicdiscovery.server.business.service.browse.BrowseServiceManager;

import java.io.IOException;

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
        browseMenuManager.addDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, LastFMImage.class.getSimpleName(), "%object.name");
        browseMenuManager.addDefaultItemFormat(BrowseMenuManager.MenuType.LIBRARY, LastFMImage.class.getSimpleName(), "%object.name");

        browseMenuManager.addCommand("lastfmimportimage", LastFMImport.class);

        browseServiceManager.addBrowseService(LastFMArtist.class.getSimpleName(), LastFMArtistBrowseService.class, getConfiguration());
        browseServiceManager.addBrowseService(LastFMAlbum.class.getSimpleName(), LastFMAlbumBrowseService.class, getConfiguration());
        browseServiceManager.addBrowseService(LastFMTrack.class.getSimpleName(), LastFMTrackBrowseService.class, getConfiguration());
        browseServiceManager.addBrowseService(LastFMImage.class.getSimpleName(), LastFMImageBrowseService.class, getConfiguration());

        try {
            browseMenuManager.loadMenusFromXml(getClass().getResourceAsStream("/org/socialmusicdiscovery/server/plugins/mediaimport/lastfm/lastfm-menus.xml"));
            browseMenuManager.loadMenusFromXml(getClass().getResourceAsStream("/org/socialmusicdiscovery/server/plugins/mediaimport/lastfm/lastfm-experimental-menus.xml"));
        } catch (IOException e) {
            throw new PluginException(e);
        } catch (ParsingException e) {
            throw new PluginException(e);
        }
        return true;
    }

    @Override
    public void stop() throws PluginException {

        browseMenuManager.removeDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, LastFMArtist.class.getSimpleName());
        browseMenuManager.removeDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, LastFMAlbum.class.getSimpleName());
        browseMenuManager.removeDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, LastFMTrack.class.getSimpleName());
        browseMenuManager.removeDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, LastFMImage.class.getSimpleName());
        browseMenuManager.removeDefaultItemFormat(BrowseMenuManager.MenuType.LIBRARY, LastFMImage.class.getSimpleName());

        browseMenuManager.removeCommand("lastfmimportimage");

        browseServiceManager.removeBrowseService(LastFMArtist.class.getSimpleName());
        browseServiceManager.removeBrowseService(LastFMAlbum.class.getSimpleName());
        browseServiceManager.removeBrowseService(LastFMTrack.class.getSimpleName());
        browseServiceManager.removeBrowseService(LastFMImage.class.getSimpleName());
        super.stop();
    }
}
