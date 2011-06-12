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

import org.socialmusicdiscovery.server.business.logic.config.ConfigurationManager;
import org.socialmusicdiscovery.server.business.logic.config.MappedConfigurationContext;
import org.socialmusicdiscovery.server.business.logic.config.MergedConfigurationManager;
import org.socialmusicdiscovery.server.business.logic.config.PersistentConfigurationManager;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;

import java.util.*;

/**
 * The browse menu manager is responsible to manage all browse menus, both the default provided and those added by
 * third party plugins. To handle this it loads menu configuration from default configuration, {@link org.socialmusicdiscovery.server.business.repository.config.ConfigurationParameterRepository}
 * but also offers an API to add/remove menus.
 */
public class BrowseMenuManager {
    /**
     * Type of menu
     */
    public static enum MenuType {
        /**
         * Top level library menu
         */
        LIBRARY,
        /**
         * Context menu initiated from a selected object
         */
        CONTEXT
    }

    private Map<MenuType, Map<String, Menu>> menus = new HashMap<MenuType, Map<String, Menu>>();
    private Map<String, Class<? extends Command>> commands = new HashMap<String, Class<? extends Command>>();
    private Map<String, String> formats = new HashMap<String, String>();

    private ConfigurationManager configurationManager;

    public BrowseMenuManager() {
        configurationManager = new MergedConfigurationManager(new PersistentConfigurationManager());
        loadMenu(MenuType.LIBRARY);
        loadMenu(MenuType.CONTEXT);
    }

    /**
     * Loads all menus from configuration for the specified menu type
     *
     * @param menuType Menu type to load
     */
    private void loadMenu(MenuType menuType) {
        Map<String, Menu> menus = new HashMap<String, Menu>();

        MappedConfigurationContext config = new MappedConfigurationContext(getClass().getName() + "." + menuType + ".", configurationManager);

        Collection<ConfigurationParameterEntity> parametersEntities = configurationManager.getParametersByPath(getClass().getName() + "." + menuType + ".");
        Set<String> menuKeys = new HashSet<String>();
        for (ConfigurationParameterEntity entity : parametersEntities) {
            String id = entity.getId().substring((getClass().getName() + "." + menuType + ".").length());
            if (id.contains(".") && !id.startsWith("formats.")) {
                menuKeys.add(id.substring(0, id.indexOf(".")));
            }
        }

        for (String menuId : menuKeys) {
            if (config.getBooleanParameter(menuId + ".enabled", true)) {
                String menuName = config.getStringParameter(menuId + ".name");
                List<MenuLevel> levels = new ArrayList<MenuLevel>();
                int i = 1;
                while (configurationManager.getParametersByPath(getClass().getName() + "." + menuType + "." + menuId + "." + i).size() > 0) {
                    String objectType = config.getStringParameter(menuId + "." + i + ".type");
                    String format = config.getStringParameter(menuId + "." + i + ".format");
                    if (format == null) {
                        format = getDefaultItemFormat(menuType, objectType);
                    }

                    Boolean playable = config.getBooleanParameter(menuId + "." + i + ".playable", false);
                    Integer criteriaDepth = config.getIntegerParameter(menuId + "." + i + ".criteriaDepth");

                    if (objectType != null && format != null && playable != null) {
                        if (criteriaDepth != null) {
                            levels.add(new MenuLevel(objectType, format, playable, criteriaDepth.longValue()));
                        } else {
                            levels.add(new MenuLevel(objectType, format, playable));
                        }
                    }
                    i++;
                }
                if (levels.size() > 0 && menuId != null && menuName != null) {
                    Integer weight = config.getIntegerParameter(menuId + ".weight", Menu.MIDDLE_WEIGHT);
                    String contextConfig = config.getStringParameter(menuId + ".context");
                    if (contextConfig != null) {
                        String[] contexts = contextConfig.split(",");
                        for (String context : contexts) {
                            menus.put(context + "." + menuId, new Menu(context, menuId, menuName, weight, levels));
                        }
                    } else {
                        menus.put(menuId, new Menu(menuId, menuName, weight, levels));
                    }
                }
            }
        }
        this.menus.put(menuType, menus);
    }

    /**
     * Add new default item format, this will be used of now specific format has been configured on a specific menu item
     *
     * @param menuType   Type of menu
     * @param objectType Type of item
     * @param format     The format to use, see {@link org.socialmusicdiscovery.server.support.format.TitleFormat} for more information
     */
    public void addDefaultItemFormat(MenuType menuType, String objectType, String format) {
        formats.put(menuType + "." + objectType, format);
    }

    /**
     * Remove a previously existed default item format
     *
     * @param menuType   Type of menu
     * @param objectType Type of item
     */
    public void removeDefaultItemFormat(MenuType menuType, String objectType) {
        formats.remove(menuType + "." + objectType);
    }

    /**
     * Get the default item format for the specified object type
     *
     * @param menuType   Menu type to get format for
     * @param objectType Object type to get format for
     * @return The default item format for the object type or null if no default format exist
     */
    public String getDefaultItemFormat(MenuType menuType, String objectType) {
        if (!this.formats.containsKey(menuType + "." + objectType)) {
            MappedConfigurationContext formatConfigs = new MappedConfigurationContext(getClass().getName() + "." + menuType + ".formats.", configurationManager);

            String format = formatConfigs.getStringParameter(objectType);
            if (format == null && objectType.contains(".")) {
                format = formatConfigs.getStringParameter(objectType.substring(0, objectType.indexOf(".")));
            }
            if (format != null) {
                this.formats.put(menuType + "." + objectType, format);
            }
        }
        return this.formats.get(menuType + "." + objectType);
    }

    /**
     * Add a new menu for the specified menu type
     *
     * @param menuType Menu type to add menu in
     * @param menu     Menu to add
     */
    public void addMenu(MenuType menuType, Menu menu) {
        for (MenuLevel level : menu.getHierarchy()) {
            if (level.getFormat() == null) {
                level.setFormat(getDefaultItemFormat(menuType, level.getType()));
            }
        }
        if (menu.getContext() != null) {
            this.menus.get(menuType).put(menu.getContext() + "." + menu.getId(), menu);
        } else {
            this.menus.get(menuType).put(menu.getId(), menu);
        }
    }

    /**
     * Remove a previously added menu
     *
     * @param menuType The menu type the menu was registered for
     * @param id       The id of the menu, see {@link Menu#id}
     */
    public void removeMenu(MenuType menuType, String id) {
        this.menus.get(menuType).remove(id);
    }

    /**
     * Remove a previously added menu
     *
     * @param menuType The menu type the menu was registered for
     * @param context  The context the menu was registered in, see {@link Menu#context}
     * @param id       The id of the menu, see {@link Menu#id}
     */
    public void removeMenu(MenuType menuType, String context, String id) {
        this.menus.get(menuType).remove(context + "." + id);
    }

    /**
     * Get all menus for the specified menu type
     *
     * @param menuType The typ of menus to get
     * @return A sorted list of menus which are available for the specified item type
     */
    public Collection<Menu> getAllMenus(MenuType menuType) {
        List<Menu> menus = new ArrayList<Menu>(this.menus.get(menuType).values());
        Collections.sort(menus, new Comparator<Menu>() {
            @Override
            public int compare(Menu m1, Menu m2) {
                int weight = m1.getWeight().compareTo(m2.getWeight());
                if (weight == 0) {
                    return m1.getName().compareTo(m2.getName());
                } else {
                    return weight;
                }
            }
        });
        return menus;
    }

    public void addCommand(String commandId, Class<? extends Command> browseCommandService) {
        this.commands.put(commandId, browseCommandService);

    }

    public void removeCommand(String commandId) {
        this.commands.remove(commandId);
    }

    public Command getCommand(String commandId) {
        try {
            return this.commands.get(commandId).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
