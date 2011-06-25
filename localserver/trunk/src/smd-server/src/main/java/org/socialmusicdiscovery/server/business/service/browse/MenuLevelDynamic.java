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
 * Represents a browse menu level which is dynamic which items comes from a {@link BrowseService} implementation provided
 * through {@link BrowseServiceManager}
 */
public class MenuLevelDynamic extends AbstractMenuLevel {
    /**
     * Title format which should be used when formatting the text for this menu, see {@link org.socialmusicdiscovery.server.support.format.TitleFormat}
     */
    private String format = null;

    /**
     * Indicates if this menu item is playable
     */
    private Boolean playable;
    /**
     * Indicates how many parent menu levels that should be included when filtering menu contents.
     * If set to null, all parent menu levels will be included.
     */
    private Long criteriaDepth = null;

    public MenuLevelDynamic() {
    }

    /**
     * Constructs a new instance
     *
     * @param type        Type of menu item, see {@link #type}
     * @param format      The title format string to use when formatting this menu item, see {@link org.socialmusicdiscovery.server.support.format.TitleFormat}
     * @param playable    Indicates if menu is playable
     * @param childLevels Child levels below this menu level
     */
    public MenuLevelDynamic(String type, String format, Boolean playable, List<MenuLevel> childLevels) {
        super(type, childLevels);
        this.playable = playable;
        this.format = format;
    }

    /**
     * Constructs a new instance
     *
     * @param type       Type of menu item, see {@link #type}
     * @param format     The title format string to use when formatting this menu item, see {@link org.socialmusicdiscovery.server.support.format.TitleFormat}
     * @param playable   Indicates if menu is playable
     * @param childLevel Child level below this menu level
     */
    public MenuLevelDynamic(String type, String format, Boolean playable, MenuLevel childLevel) {
        this(type, format, playable, Arrays.asList(childLevel));
    }

    /**
     * Constructs a new instance
     *
     * @param type     Type of menu item, see {@link #type}
     * @param format   The title format string to use when formatting this menu item, see {@link org.socialmusicdiscovery.server.support.format.TitleFormat}
     * @param playable Indicates if menu is playable
     */
    public MenuLevelDynamic(String type, String format, Boolean playable) {
        this(type, format, playable, (List<MenuLevel>) null);
    }

    /**
     * Constructs a new instance with default format based on the specified type
     *
     * @param type          Type of menu item, see {@link #type}
     * @param format        Title format that should be used, use null for default format based on {@link #type}
     * @param playable      Indicates if menu is playable
     * @param criteriaDepth Number of parent levels that should be included when filtering this level
     * @param childLevel    Child level below this menu level
     */
    public MenuLevelDynamic(String type, String format, Boolean playable, Long criteriaDepth, MenuLevel childLevel) {
        this(type, format, playable, Arrays.asList(childLevel));
        setCriteriaDepth(criteriaDepth);
    }

    /**
     * Constructs a new instance
     *
     * @param type          Type of menu item, see {@link #type}
     * @param format        The title format string to use when formatting this menu item, see {@link org.socialmusicdiscovery.server.support.format.TitleFormat}
     * @param playable      Indicates if menu is playable
     * @param criteriaDepth Number of parent levels that should be included when filtering this level
     */
    public MenuLevelDynamic(String type, String format, Boolean playable, Long criteriaDepth) {
        this(type, format, playable);
        setCriteriaDepth(criteriaDepth);
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Boolean isPlayable() {
        return playable;
    }

    public Long getCriteriaDepth() {
        return criteriaDepth;
    }

    public void setCriteriaDepth(Long criteriaDepth) {
        this.criteriaDepth = criteriaDepth;
    }

    public String getId() {
        return getType();
    }

    public String getDisplayName() {
        return getType();
    }
}
