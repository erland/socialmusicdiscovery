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

package org.socialmusicdiscovery.server.business.service.browse;

import java.util.*;

public class ContextBrowseService extends LibraryBrowseService {
    protected List<Menu> getMenuHierarchy() {
        List<Menu> menus = new ArrayList<Menu>();

        menus.add(new Menu("Track","artists.performers", "Performers", Arrays.asList(
                new MenuLevel("Artist.performer", "%object.name", true),
                new MenuLevel("Release", "%object.name", true,1L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,2L))));
        menus.add(new Menu("Track","artists.composers", "Composers", Arrays.asList(
                new MenuLevel("Artist.composer", "%object.name", true),
                new MenuLevel("Release", "%object.name", true,1L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,2L))));
        menus.add(new Menu("Track","artists.conductors", "Conductors", Arrays.asList(
                new MenuLevel("Artist.conductor", "%object.name", true),
                new MenuLevel("Release", "%object.name", true,1L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,2L))));
        menus.add(new Menu("Track","releases", "Releases", Arrays.asList(
                new MenuLevel("Release", "%object.name", true),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,1L))));
        menus.add(new Menu("Track","classifications.genres", "Genres", Arrays.asList(
                new MenuLevel("Classification.genre", "%object.name", false),
                new MenuLevel("Artist", "%object.name", true,1L),
                new MenuLevel("Release", "%object.name", true,2L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,3L))));
        menus.add(new Menu("Track","classifications.styles", "Styles", Arrays.asList(
                new MenuLevel("Classification.style", "%object.name", false),
                new MenuLevel("Artist", "%object.name", true,1L),
                new MenuLevel("Release", "%object.name", true,2L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,3L))));
        menus.add(new Menu("Track","classifications.moods", "Moods", Arrays.asList(
                new MenuLevel("Classification.mood", "%object.name", false),
                new MenuLevel("Artist", "%object.name", true,1L),
                new MenuLevel("Release", "%object.name", true,2L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,3L))));

        menus.add(new Menu("Release","artists.performers", "Performers", Arrays.asList(
                new MenuLevel("Artist.performer", "%object.name", true),
                new MenuLevel("Release", "%object.name", true,1L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,2L))));
        menus.add(new Menu("Release","artists.composers", "Composers", Arrays.asList(
                new MenuLevel("Artist.composer", "%object.name", true),
                new MenuLevel("Release", "%object.name", true,1L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,2L))));
        menus.add(new Menu("Release","artists.conductors", "Conductors", Arrays.asList(
                new MenuLevel("Artist.conductor", "%object.name", true),
                new MenuLevel("Release", "%object.name", true,1L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,2L))));
        menus.add(new Menu("Release","classifications.genres", "Genres", Arrays.asList(
                new MenuLevel("Classification.genre", "%object.name", false),
                new MenuLevel("Artist", "%object.name", true,1L),
                new MenuLevel("Release", "%object.name", true,2L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,3L))));
        menus.add(new Menu("Release","classifications.styles", "Styles", Arrays.asList(
                new MenuLevel("Classification.style", "%object.name", false),
                new MenuLevel("Artist", "%object.name", true,1L),
                new MenuLevel("Release", "%object.name", true,2L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,3L))));
        menus.add(new Menu("Release","classifications.moods", "Moods", Arrays.asList(
                new MenuLevel("Classification.mood", "%object.name", false),
                new MenuLevel("Artist", "%object.name", true,1L),
                new MenuLevel("Release", "%object.name", true,2L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,3L))));

        menus.add(new Menu("Artist","artists.performers", "Performers", Arrays.asList(
                new MenuLevel("Artist.performer", "%object.name", true),
                new MenuLevel("Release", "%object.name", true,1L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,2L))));
        menus.add(new Menu("Artist","artists.composers", "Composers", Arrays.asList(
                new MenuLevel("Artist.composer", "%object.name", true),
                new MenuLevel("Release", "%object.name", true,1L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,2L))));
        menus.add(new Menu("Artist","artists.conductors", "Conductors", Arrays.asList(
                new MenuLevel("Artist.conductor", "%object.name", true),
                new MenuLevel("Release", "%object.name", true,1L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,2L))));
        menus.add(new Menu("Artist","releases", "Releases", Arrays.asList(
                new MenuLevel("Release", "%object.name", true),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,1L))));
        menus.add(new Menu("Artist","classifications.genres", "Genres", Arrays.asList(
                new MenuLevel("Classification.genre", "%object.name", false),
                new MenuLevel("Artist", "%object.name", true,1L),
                new MenuLevel("Release", "%object.name", true,2L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,3L))));
        menus.add(new Menu("Artist","classifications.styles", "Styles", Arrays.asList(
                new MenuLevel("Classification.style", "%object.name", false),
                new MenuLevel("Artist", "%object.name", true,1L),
                new MenuLevel("Release", "%object.name", true,2L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,3L))));
        menus.add(new Menu("Artist","classifications.moods", "Moods", Arrays.asList(
                new MenuLevel("Classification.mood", "%object.name", false),
                new MenuLevel("Artist", "%object.name", true,1L),
                new MenuLevel("Release", "%object.name", true,2L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,3L))));

        menus.add(new Menu("Classification","artists.performers", "Performers", Arrays.asList(
                new MenuLevel("Artist.performer", "%object.name", true),
                new MenuLevel("Release", "%object.name", true,1L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,2L))));
        menus.add(new Menu("Classification","artists.composers", "Composers", Arrays.asList(
                new MenuLevel("Artist.composer", "%object.name", true),
                new MenuLevel("Release", "%object.name", true,1L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,2L))));
        menus.add(new Menu("Classification","artists.conductors", "Conductors", Arrays.asList(
                new MenuLevel("Artist.conductor", "%object.name", true),
                new MenuLevel("Release", "%object.name", true,1L),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,2L))));
        menus.add(new Menu("Classification","releases", "Releases", Arrays.asList(
                new MenuLevel("Release", "%object.name", true),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true,1L))));

        return menus;
    }

    // "/browse/library/artists/Artist:dafad/Release:jdfladsa"
    // "/browse/context/Release:jdfladsa/artists/Artist:dsfadf/Release:dafa"
    // "artists/Artist:dafad/Release:jdfladsa"
    // "Release:jdfladsa/artists/Artist:dsfadf/Release:dafa"

    public Result<Object> findChildren(String parentPath, Integer firstItem, Integer maxItems, Boolean counts) {
        if (counts == null) {
            counts = false;
        }

        String currentId;
        if(parentPath.contains("/")) {
            currentId = parentPath.substring(0,parentPath.indexOf("/"));
            parentPath = parentPath.substring(currentId.length()+1);
            if(parentPath.length()==0) {
                parentPath = null;
            }
        }else {
            currentId = parentPath;
            parentPath = null;
        }

        return findChildren(currentId,parentPath, firstItem, maxItems, counts);
    }
}
