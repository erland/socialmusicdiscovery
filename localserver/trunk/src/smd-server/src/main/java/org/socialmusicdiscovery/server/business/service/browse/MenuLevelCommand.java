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
 * Represents a command/action shown as a menu item
 */
public class MenuLevelCommand extends AbstractMenuLevel {
    public static final String TYPE = Command.class.getSimpleName();

    /**
     * Identity of the command related to this menu item
     */
    private String id;

    /**
     * The text that should be shown to the user for this menu item
     */
    private String name;

    /**
     * The parameters which should be passed to the command
     */
    private List<String> parameters;

    public MenuLevelCommand() {
    }

    /**
     * Constructs a new instance
     *
     * @param id   Identity of the command
     * @param name The text to display to user
     */
    public MenuLevelCommand(String id, String name) {
        super(TYPE, null);
        this.id = id;
        this.name = name;
    }

    /**
     * Constructs a new instance
     *
     * @param id   Identity of the command
     * @param name The text to display to user
     */
    public MenuLevelCommand(String id, String name, List<String> parameters) {
        super(TYPE, null);
        this.id = id;
        this.name = name;
        this.parameters = parameters;
    }

    /**
     * Constructs a new instance
     *
     * @param context The context in which this menu item should be available
     * @param id      Identity of the command
     * @param name    The text to display to user
     */
    public MenuLevelCommand(String context, String id, String name) {
        this(id, name);
        setContext(context);
    }

    /**
     * Constructs a new instance
     *
     * @param context The context in which this menu item should be available
     * @param id      Identity of the command
     * @param name    The text to display to user
     */
    public MenuLevelCommand(String context, String id, String name, List<String> parameters) {
        this(id, name);
        setContext(context);
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String getId() {
        return getType() + ":" + id;
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public Boolean isPlayable() {
        return false;
    }
}
