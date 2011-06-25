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
 * Represents a browse menu level
 */
public interface MenuLevel {
    public static final int TOP_WEIGHT = 1;
    public static final int UPPER_WEIGHT = 25;
    public static final int MIDDLE_WEIGHT = 50;
    public static final int LOWER_WEIGHT = 75;
    public static final int BOTTOM_WEIGHT = 100;

    public String getType();

    public void setType(String type);

    public List<MenuLevel> getChildLevels();

    public void setChildLevels(List<MenuLevel> childLevels);

    public Integer getWeight();

    public void setWeight(Integer weight);

    public String getContext();

    public void setContext(String context);

    public String getId();

    /**
     * The text that should be displayed for items provided through this menu level, typically only used for static items such as
     * {@link MenuLevelCommand} and {@link MenuLevelDynamic}
     * @return The text to display
     */
    public String getDisplayName();

    /**
     * Indicates if items provided by this menu level should be playable
     * @return true if playable else false
     */
    public Boolean isPlayable();

}
