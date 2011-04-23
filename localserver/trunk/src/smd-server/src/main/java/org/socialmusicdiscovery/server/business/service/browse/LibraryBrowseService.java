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

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.support.format.TitleFormat;

import java.util.*;

public class LibraryBrowseService {
    @Inject
    ObjectTypeBrowseService objectTypeBrowseService;

    private static class MenuLevel {
        String type;
        String format;
        Boolean playable;

        MenuLevel(String type, String format, Boolean playable) {
            this.type = type;
            this.format = format;
            this.playable = playable;
        }
    }

    private static class Menu {
        String name;
        String id;
        List<MenuLevel> hierarchy;

        Menu(String id, String name, List<MenuLevel> hierarchy) {
            this.id = id;
            this.name = name;
            this.hierarchy = hierarchy;
        }
    }

    private List<Menu> menus = new ArrayList<Menu>();

    public LibraryBrowseService() {
        InjectHelper.injectMembers(this);
        menus.add(new Menu("artists", "Artists", Arrays.asList(
                new MenuLevel("Artist", "%object.name", true),
                new MenuLevel("Release", "%object.name", true),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true))));
        menus.add(new Menu("artists.composers", "Composers", Arrays.asList(
                new MenuLevel("Artist.composer", "%object.name", true),
                new MenuLevel("Release", "%object.name", true),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true))));
        menus.add(new Menu("artists.conductors", "Conductors", Arrays.asList(
                new MenuLevel("Artist.conductor", "%object.name", true),
                new MenuLevel("Release", "%object.name", true),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true))));
        menus.add(new Menu("releases", "Releases", Arrays.asList(
                new MenuLevel("Release", "%object.name", true),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true))));
        menus.add(new Menu("classifications.genres", "Genres", Arrays.asList(
                new MenuLevel("Classification.genre", "%object.name", false),
                new MenuLevel("Artist", "%object.name", true),
                new MenuLevel("Release", "%object.name", true),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true))));
        menus.add(new Menu("classifications.styles", "Styles", Arrays.asList(
                new MenuLevel("Classification.style", "%object.name", false),
                new MenuLevel("Artist", "%object.name", true),
                new MenuLevel("Release", "%object.name", true),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true))));
        menus.add(new Menu("classifications.moods", "Moods", Arrays.asList(
                new MenuLevel("Classification.mood", "%object.name", false),
                new MenuLevel("Artist", "%object.name", true),
                new MenuLevel("Release", "%object.name", true),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", true))));
    }


    public Result<Object> findChildren(Integer firstItem, Integer maxItems) {
        return findChildren(null, firstItem, maxItems, false);
    }

    public Result<Object> findChildren(Integer firstItem, Integer maxItems, Boolean counts) {
        return findChildren(null, firstItem, maxItems, counts);
    }

    public Result<Object> findChildren(String parentPath, Integer firstItem, Integer maxItems) {
        return findChildren(parentPath, firstItem, maxItems, false);
    }

    public Result<Object> findChildren(String parentPath, Integer firstItem, Integer maxItems, Boolean counts) {
        Result<Object> result = new Result<Object>();
        if (counts == null) {
            counts = false;
        }
        if (parentPath == null) {
            List<ResultItem<Object>> items = new ArrayList<ResultItem<Object>>();
            result.setItems(items);
            List<Menu> menus = this.menus;
            int i = 0;
            Map<String, Long> counters = null;
            if (counts) {
                counters = objectTypeBrowseService.findObjectTypes(new ArrayList<String>(), true);
            }
            for (Menu menu : menus) {
                if ((firstItem == null || firstItem <= i) && (maxItems == null || maxItems > items.size())) {
                    if (counts) {
                        Map<String, Long> childCounters = new HashMap<String, Long>(1);
                        if (counters.get(menu.hierarchy.get(0).type) != null) {
                            childCounters.put(menu.hierarchy.get(0).type, counters.get(menu.hierarchy.get(0).type));
                        }
                        items.add(new ResultItem<Object>(menu.name, "Folder", menu.id, menu.name, false, childCounters));
                    } else {
                        items.add(new ResultItem<Object>(menu.name, "Folder", menu.id, menu.name, false, false));
                    }
                }
                i++;
            }
            result.setCount((long) menus.size());
        } else {
            String currentPath = parentPath;
            Menu currentMenu = null;

            for (Menu menu : menus) {
                if (currentPath.equals(menu.id) || currentPath.contains("/") && currentPath.substring(0, currentPath.indexOf("/")).equals(menu.id)) {
                    currentMenu = menu;
                    currentPath = currentPath.substring(menu.id.length());
                    if (currentPath.startsWith(".")) {
                        currentPath = currentPath.substring(1);
                    }
                    break;
                }
            }

            if (currentMenu != null) {
                Map<String, String> criteriaMap = new HashMap<String, String>();
                StringTokenizer tokens = new StringTokenizer(currentPath, "/");
                while (tokens.hasMoreElements()) {
                    String token = tokens.nextToken();
                    if (token.contains(":")) {
                        criteriaMap.put(token.substring(0, token.indexOf(":")), token.substring(token.indexOf(":") + 1));
                    } else {
                        criteriaMap.put(token, null);
                    }
                }

                List<String> criterias = new ArrayList<String>();
                String requestedMainObjectType = null;
                MenuLevel requestedObjectType = null;
                String nextObjectType = null;
                for (MenuLevel objectType : currentMenu.hierarchy) {
                    String value = criteriaMap.get(objectType.type);
                    if (value != null) {
                        criterias.add(objectType.type + ":" + value);
                    } else {
                        if (currentMenu.hierarchy.size() > criterias.size() + 1) {
                            nextObjectType = currentMenu.hierarchy.get(criterias.size() + 1).type;
                        }
                        requestedObjectType = objectType;
                        if (objectType.type.contains(".")) {
                            requestedMainObjectType = objectType.type.substring(0, objectType.type.indexOf("."));
                            criterias.add(objectType.type);
                        } else {
                            requestedMainObjectType = objectType.type;
                        }
                        break;
                    }
                }

                if (requestedMainObjectType != null) {
                    TitleFormat parser = new TitleFormat(requestedObjectType.format);

                    BrowseService browseService = InjectHelper.instanceWithName(BrowseService.class, requestedMainObjectType);
                    Result browseResult = browseService.findChildren(criterias, new ArrayList<String>(), firstItem, maxItems, counts);
                    Collection<ResultItem> browseResultItems = browseResult.getItems();
                    for (ResultItem item : browseResultItems) {
                        String id = null;
                        if (item.getItem() instanceof SMDIdentity) {
                            id = requestedObjectType.type + ":" + ((SMDIdentity) item.getItem()).getId();
                        } else {
                            id = requestedObjectType.type + ":" + item.getItem().toString();
                        }
                        item.setId(id);
                        item.setType(requestedMainObjectType);
                        item.setName(parser.format(item.getItem()));
                        item.setPlayable(requestedObjectType.playable);
                        item.setLeaf(nextObjectType==null);

                        if (counts) {
                            Map<String, Long> childCounters = new HashMap<String, Long>();
                            if (nextObjectType != null && item.getChildItems().containsKey(nextObjectType)) {
                                childCounters.put(nextObjectType, (Long) item.getChildItems().get(nextObjectType));
                            }
                            item.setChildItems(childCounters);
                        }
                    }
                    result = browseResult;
                }
            }
        }
        return result;
    }
}

