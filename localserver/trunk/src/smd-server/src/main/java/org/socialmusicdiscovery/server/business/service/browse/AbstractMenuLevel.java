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

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class used by all {@link MenuLevel} implementations
 */
public abstract class AbstractMenuLevel implements MenuLevel {
    /**
     * Type of item, typically the same as returned from {@link org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity#forEntity(org.socialmusicdiscovery.server.business.model.SMDIdentity)}
     */
    private String type;

    /**
     * Weight which affect sorting, lower value means that it's placed on the top and higher value on the bottom.
     */
    private Integer weight;
    /**
     * Only set for context menus, indicates which object type this menu should be shown for, see {@link org.socialmusicdiscovery.server.business.service.browse.MenuLevel#getType()}
     */
    private String context = null;

    /**
     * Contains list of menu items below this menu item
     */
    private List<MenuLevel> childLevels;

    public AbstractMenuLevel() {
    }

    /**
     * Constructs a new instance with default format based on the specified type
     *
     * @param type        Type of menu item, see {@link #type}
     * @param childLevels Child levels below this menu level
     */
    public AbstractMenuLevel(String type, List<MenuLevel> childLevels) {
        this.weight = MIDDLE_WEIGHT;
        this.type = type;
        if (childLevels != null) {
            this.childLevels = new ArrayList<MenuLevel>(childLevels);
        } else {
            this.childLevels = null;
        }
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<MenuLevel> getChildLevels() {
        return childLevels;
    }

    public void setChildLevels(List<MenuLevel> childLevels) {
        this.childLevels = childLevels;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
