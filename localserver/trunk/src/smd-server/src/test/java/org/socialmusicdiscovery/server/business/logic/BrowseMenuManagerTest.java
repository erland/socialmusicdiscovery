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

package org.socialmusicdiscovery.server.business.logic;

import org.socialmusicdiscovery.server.business.service.browse.*;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BrowseMenuManagerTest extends BaseTestCase {
    BrowseMenuManager browseMenuManager;

    @BeforeMethod
    public void initBrowseMenuManager() {
        browseMenuManager = new BrowseMenuManager();
    }

    public void testDefaultLibrary() {
        browseMenuManager = new BrowseMenuManager();
        List<MenuLevel> menus = browseMenuManager.getAllMenus(BrowseMenuManager.MenuType.LIBRARY);
        findMenu(Arrays.asList("Folder:artists", "Artist", "Release", "Track"), menus);
        findMenu(Arrays.asList("Folder:composers", "Artist.composer", "Release", "Track"), menus);
        findMenu(Arrays.asList("Folder:conductors", "Artist.conductor", "Release", "Track"), menus);
        findMenu(Arrays.asList("Folder:releases", "Release", "Track"), menus);
        findMenu(Arrays.asList("Folder:genres", "Classification.genre", "Artist", "Release", "Track"), menus);
        findMenu(Arrays.asList("Folder:styles", "Classification.style", "Artist", "Release", "Track"), menus);
        findMenu(Arrays.asList("Folder:moods", "Classification.mood", "Artist", "Release", "Track"), menus);
    }

    @Test
    public void testAddSingle() {
        browseMenuManager.addMenu(BrowseMenuManager.MenuType.LIBRARY,
                new MenuLevelFolder("addSingle", "AddSingle",
                        new MenuLevelDynamic("Artist", null, true,
                                new MenuLevelDynamic("Release", null, true,
                                        new MenuLevelDynamic("Track", null, true)))));
        findMenu(Arrays.asList("Folder:addSingle", "Artist", "Release", "Track"), browseMenuManager.getAllMenus(BrowseMenuManager.MenuType.LIBRARY));
    }

    @Test
    public void testAddSingleCommand() {
        browseMenuManager.addMenu(BrowseMenuManager.MenuType.LIBRARY,
                new MenuLevelFolder("addSingleCommand", "AddSingleCommand",
                        new MenuLevelDynamic("Artist", null, true,
                                new MenuLevelDynamic("Release", null, true,
                                        new MenuLevelCommand("play", "Play")))));
        findMenu(Arrays.asList("Folder:addSingleCommand", "Artist", "Release", "Command:play"), browseMenuManager.getAllMenus(BrowseMenuManager.MenuType.LIBRARY));
    }

    @Test
    public void testAddSingleFolder() {
        browseMenuManager.addMenu(BrowseMenuManager.MenuType.LIBRARY,
                new MenuLevelFolder("firstFolder", "First Folder",
                        new MenuLevelDynamic("Artist", null, true,
                                new MenuLevelFolder("releases", "Releases",
                                        new MenuLevelDynamic("Release", null, true,
                                                new MenuLevelDynamic("Track", null, true))))));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.LIBRARY,
                new MenuLevelFolder("secondFolder", "Second Folder",
                        new MenuLevelFolder("specialReleases", "Special Releases",
                                new MenuLevelDynamic("Release", null, true,
                                        new MenuLevelDynamic("Track", null, true)))));

        findMenu(Arrays.asList("Folder:firstFolder", "Artist", "Folder:releases", "Release", "Track"), browseMenuManager.getAllMenus(BrowseMenuManager.MenuType.LIBRARY));
        findMenu(Arrays.asList("Folder:secondFolder", "Folder:specialReleases", "Release", "Track"), browseMenuManager.getAllMenus(BrowseMenuManager.MenuType.LIBRARY));
    }

    @Test
    public void testAddMultipleCommands() {
        browseMenuManager.addMenu(BrowseMenuManager.MenuType.LIBRARY,
                new MenuLevelFolder("commandMenu", "CommandMenu",
                        new MenuLevelDynamic("Artist", null, true,
                                new MenuLevelDynamic("Release", null, true,
                                        new MenuLevelDynamic("Track", null, true,
                                                new MenuLevelCommand("play", "Play"))))));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.LIBRARY,
                new MenuLevelFolder("commandMenu", "CommandMenu",
                        new MenuLevelDynamic("Artist", null, true,
                                new MenuLevelDynamic("Release", null, true,
                                        new MenuLevelDynamic("Track", null, true,
                                                new MenuLevelCommand("add", "Add"))))));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.LIBRARY,
                new MenuLevelFolder("commandMenu", "CommandMenu",
                        new MenuLevelDynamic("Artist", null, true,
                                new MenuLevelDynamic("Release", null, true,
                                        new MenuLevelCommand("play", "Play")))));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.LIBRARY,
                new MenuLevelFolder("commandMenu", "CommandMenu",
                        new MenuLevelDynamic("Artist", null, true,
                                new MenuLevelCommand("play", "Play"))));

        findMenu(Arrays.asList("Folder:commandMenu", "Artist", "Release", "Track", "Command:play"), browseMenuManager.getAllMenus(BrowseMenuManager.MenuType.LIBRARY));
        findMenu(Arrays.asList("Folder:commandMenu", "Artist", "Release", "Track", "Command:add"), browseMenuManager.getAllMenus(BrowseMenuManager.MenuType.LIBRARY));
        findMenu(Arrays.asList("Folder:commandMenu", "Artist", "Release", "Command:play"), browseMenuManager.getAllMenus(BrowseMenuManager.MenuType.LIBRARY));
        findMenu(Arrays.asList("Folder:commandMenu", "Artist", "Command:play"), browseMenuManager.getAllMenus(BrowseMenuManager.MenuType.LIBRARY));
    }

    @Test
    public void testAddContextMultipleCommands() {
        browseMenuManager.addMenu(BrowseMenuManager.MenuType.CONTEXT,
                new MenuLevelFolder("MyObject", "commandMenu", "CommandMenu", MenuLevel.MIDDLE_WEIGHT,
                        new MenuLevelDynamic("Artist", null, true,
                                new MenuLevelDynamic("Release", null, true,
                                        new MenuLevelDynamic("Track", null, true,
                                                new MenuLevelCommand("play", "Play"))))));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.CONTEXT,
                new MenuLevelFolder("MyObject", "commandMenu", "CommandMenu", MenuLevel.MIDDLE_WEIGHT,
                        new MenuLevelDynamic("Artist", null, true,
                                new MenuLevelDynamic("Release", null, true,
                                        new MenuLevelDynamic("Track", null, true,
                                                new MenuLevelCommand("add", "Add"))))));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.CONTEXT,
                new MenuLevelFolder("MyObject", "commandMenu", "CommandMenu", MenuLevel.MIDDLE_WEIGHT,
                        new MenuLevelDynamic("Artist", null, true,
                                new MenuLevelDynamic("Release", null, true,
                                        new MenuLevelCommand("play", "Play")))));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.CONTEXT,
                new MenuLevelFolder("MyObject", "commandMenu", "CommandMenu", MenuLevel.MIDDLE_WEIGHT,
                        new MenuLevelDynamic("Artist", null, true,
                                new MenuLevelCommand("play", "Play"))));

        browseMenuManager.addMenu(BrowseMenuManager.MenuType.CONTEXT,
                new MenuLevelFolder("MyObject", "commandMenu", "CommandMenu", MenuLevel.MIDDLE_WEIGHT,
                        new MenuLevelCommand("play", "Play")));

        findMenu("MyObject", Arrays.asList("Folder:commandMenu", "Artist", "Release", "Track", "Command:play"), browseMenuManager.getAllMenus(BrowseMenuManager.MenuType.CONTEXT, "MyObject"));
        findMenu("MyObject", Arrays.asList("Folder:commandMenu", "Artist", "Release", "Track", "Command:add"), browseMenuManager.getAllMenus(BrowseMenuManager.MenuType.CONTEXT, "MyObject"));
        findMenu("MyObject", Arrays.asList("Folder:commandMenu", "Artist", "Release", "Command:play"), browseMenuManager.getAllMenus(BrowseMenuManager.MenuType.CONTEXT, "MyObject"));
        findMenu("MyObject", Arrays.asList("Folder:commandMenu", "Artist", "Command:play"), browseMenuManager.getAllMenus(BrowseMenuManager.MenuType.CONTEXT, "MyObject"));
        findMenu("MyObject", Arrays.asList("Folder:commandMenu", "Command:play"), browseMenuManager.getAllMenus(BrowseMenuManager.MenuType.CONTEXT, "MyObject"));
    }

    private void findMenu(List<String> path, List<MenuLevel> menus) {
        findMenu(null, path, menus);
    }

    private void findMenu(String context, List<String> path, List<MenuLevel> menus) {
        MenuLevel level = findMenuLevel(context, path, menus);
        if (level == null) {
            Assert.fail("Cannot find: " + Arrays.toString(path.toArray()));
        }
    }

    private MenuLevel findMenuLevel(String context, List<String> path, List<MenuLevel> levels) {
        for (MenuLevel level : levels) {
            if (context != null && (level.getContext() == null || !level.getContext().equals(context))) {
                continue;
            }
            if (path.get(0).startsWith(level.getId() + ":") || path.get(0).equals(level.getId())) {
                List<String> subPath = new ArrayList<String>(path);
                subPath.remove(0);
                if (subPath.size() == 0) {
                    return level;
                } else {
                    return findMenuLevel(null, subPath, level.getChildLevels());
                }
            }
        }
        return null;
    }

    private void printMenus(List<MenuLevel> menus) {
        printMenuLevels(menus, 2);
    }

    private void printMenuLevels(List<MenuLevel> levels, Integer indent) {
        for (MenuLevel level : levels) {
            for (int i = 0; i < indent; i++) {
                System.out.print(" ");
            }
            if (level instanceof MenuLevelCommand) {
                System.out.println(level.getType() + ":" + ((MenuLevelCommand) level).getName());
            } else if (level instanceof MenuLevelFolder) {
                System.out.println(level.getType() + ":" + ((MenuLevelFolder) level).getName());
            } else {
                System.out.println(level.getType());
            }
            if (level.getChildLevels() != null) {
                printMenuLevels(level.getChildLevels(), indent + 2);
            }
        }
    }
}
