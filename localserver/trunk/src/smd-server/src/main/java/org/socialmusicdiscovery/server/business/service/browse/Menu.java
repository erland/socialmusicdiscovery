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

import java.util.List;

/**
 * Represents a browse menu
 */
public class Menu {
    public static final int TOP_WEIGHT = 1;
    public static final int UPPER_WEIGHT = 25;
    public static final int MIDDLE_WEIGHT = 50;
    public static final int LOWER_WEIGHT = 75;
    public static final int BOTTOM_WEIGHT = 100;

    /**
     * Text to display for the top level item of this menu
     */
    private String name;
    /**
     * Weight which affect sorting, lower value means that it's placed on the top and higher value on the bottom.
     */
    private Integer weight;
    /**
     * Unique identity for this menu
     */
    private String id;
    /**
     * Only set for context menus, indicates which object type this menu should be shown for, see {@link MenuLevel#type}
     */
    private String context = null;
    /**
     * The complete menu hierarchy of this menu
     */
    private List<MenuLevel> hierarchy;


    /**
     * Construct a new instance for a top level menu
     * @param id Identity of menu
     * @param name Name of menu, the displayed text
     * @param weight Weight of menu, controls sort order
     * @param hierarchy The complete menu hierarchy of this menu
     */
    public Menu(String id, String name, Integer weight, List<MenuLevel> hierarchy) {
        this.id = id;
        this.name = name;
        this.hierarchy = hierarchy;
        this.weight = weight;
    }

    /**
     * Construct a new instance for a context menu
     * @param context The type of item this menus should be displayed on, see {@link MenuLevel#type}
     * @param id Identity of menu
     * @param name Name of menu, the displayed text
     * @param weight Weight of menu, controls sort order
     * @param hierarchy The complete menu hierarchy of this menu
     */
    public Menu(String context, String id, String name, Integer weight, List<MenuLevel> hierarchy) {
        this.id = id;
        this.name = name;
        this.hierarchy = hierarchy;
        this.context = context;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public List<MenuLevel> getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(List<MenuLevel> hierarchy) {
        this.hierarchy = hierarchy;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
