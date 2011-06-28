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

import java.util.Arrays;
import java.util.List;

/**
 * Represents a static browse menu level which have child levels
 */
public class MenuLevelFolder extends AbstractMenuLevel {
    public static final String TYPE = "Folder";

    /**
     * The identity of this menu level item
     */
    private String id;

    public MenuLevelFolder() {
    }

    /**
     * Constructs a new instance
     *
     * @param id          Unique identity of this folder in the scope of this level in the menu hierarchy
     * @param label        The text that should be displayed to the user for this menu item
     * @param childLevels The child levels below this menu level
     */
    public MenuLevelFolder(String id, String label, List<MenuLevel> childLevels) {
        super(TYPE, label, childLevels);
        this.id = id;
    }

    /**
     * Constructs a new instance
     *
     * @param id         Unique identity of this folder in the scope of this level in the menu hierarchy
     * @param label       The text that should be displayed to the user for this menu item
     * @param childLevel The child level below this menu level
     */
    public MenuLevelFolder(String id, String label, MenuLevel childLevel) {
        this(id, label, Arrays.asList(childLevel));
    }

    /**
     * Constructs a new instance
     *
     * @param id         Unique identity of this folder in the scope of this level in the menu hierarchy
     * @param label       The text that should be displayed to the user for this menu item
     * @param weight     The sorting weight of this menu item
     * @param childLevel The child levels below this menu level
     */
    public MenuLevelFolder(String id, String label, Integer weight, MenuLevel childLevel) {
        this(id, label, childLevel);
        setWeight(weight);
    }

    /**
     * Constructs a new instance
     *
     * @param context    The context in which this menu item should be available
     * @param id         Unique identity of this folder in the scope of this level in the menu hierarchy
     * @param label       The text that should be displayed to the user for this menu item
     * @param weight     The sorting weight of this menu item
     * @param childLevel The child levels below this menu level
     */
    public MenuLevelFolder(String context, String id, String label, Integer weight, MenuLevel childLevel) {
        this(id, label, childLevel);
        setContext(context);
        setWeight(weight);
    }

    @Override
    public String getId() {
        return getType() + ":" + id;
    }

    @Override
    public String getDisplayName() {
        return getLabel();
    }

    @Override
    public Boolean isPlayable() {
        return false;
    }
}
