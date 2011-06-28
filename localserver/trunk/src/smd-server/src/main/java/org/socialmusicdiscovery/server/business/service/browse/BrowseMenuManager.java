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
import com.google.inject.name.Named;
import nu.xom.*;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.config.*;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import java.io.IOException;
import java.io.InputStream;
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

    /**
     * Default value in-memory configuration manager
     */
    @Inject
    @Named("default-value")
    MemoryConfigurationManager defaultValueConfigurationManager;

    private Map<MenuType, List<MenuLevel>> menus = new HashMap<MenuType, List<MenuLevel>>();
    private Map<String, Class<? extends Command>> commands = new HashMap<String, Class<? extends Command>>();
    private Map<String, String> formats = new HashMap<String, String>();

    private ConfigurationManager configurationManager;

    public BrowseMenuManager() {
        InjectHelper.injectMembers(this);
        configurationManager = new MergedConfigurationManager(new PersistentConfigurationManager());
        this.menus.put(MenuType.CONTEXT,new ArrayList<MenuLevel>());
        this.menus.put(MenuType.LIBRARY,new ArrayList<MenuLevel>());
        try {
            loadMenusFromXml(getClass().getResourceAsStream("/org/socialmusicdiscovery/server/business/service/browse/menus.xml"));
            loadMenusFromXml(getClass().getResourceAsStream("/org/socialmusicdiscovery/server/business/service/browse/contextmenus.xml"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParsingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Loads all menus from configuration from the specified menu configuration file
     *
     * @param inputStream input stream to load menu from
     */
    public void loadMenusFromXml(InputStream inputStream) throws IOException, ParsingException {
        Builder builder = new Builder();
        Document document = builder.build(inputStream);
        Element menusNode = document.getRootElement();
        if(menusNode!=null) {
            List<MenuLevel> menus = getMenusFromElement(null, menusNode);
            for (MenuLevel menu : menus) {
                if(menu.getContext()!=null) {
                    addMenu(MenuType.CONTEXT, menu);
                }else {
                    addMenu(MenuType.LIBRARY, menu);
                }
            }
        }
    }

    private List<MenuLevel> getMenusFromElement(MenuLevel parent, Element menusNode) {
        Elements menuNodes = menusNode.getChildElements("menu");
        List<MenuLevel> result = new ArrayList<MenuLevel>();
        for(int i=0;i<menuNodes.size();i++) {
            Element menu = menuNodes.get(i);
            MenuLevel level = getMenuFromElement(menu);
            Elements childMenus = menu.getChildElements("menus");
            if(childMenus.size()>0) {
                getMenusFromElement(level, childMenus.get(0));
            }
            if(parent!=null) {
                if(parent.getChildLevels()==null) {
                    parent.setChildLevels(new ArrayList<MenuLevel>());
                }
                parent.getChildLevels().add(level);
            }else {
                Nodes contexts = menu.query("contexts/context");
                if(contexts.size()>0) {
                    for(int j=0;j<contexts.size();j++) {
                        MenuLevel copy = new CopyHelper().copy(level);
                        copy.setContext((contexts.get(j)).getValue());
                        result.add(copy);
                    }
                }else {
                    result.add(level);
                }
            }
        }
        return result;
    }

    private String getMenuName(Elements labelsNode) {
        if (labelsNode == null || labelsNode.size() == 0) {
            return null;
        }

        Nodes labelNodes = labelsNode.get(0).query("label[@language='EN']");
        if (labelNodes.size() > 0) {
            return labelNodes.get(0).getValue();
        }
        return null;
    }

    private MenuLevel getMenuFromElement(Element menuNode) {
        String type = menuNode.getAttributeValue("type");
        MenuLevel level;
        if(MenuLevelCommand.TYPE.equals(type)) {
            String id = menuNode.getAttributeValue("id");
            level = new MenuLevelCommand(id,getMenuName(menuNode.getChildElements("labels")));

            List<String> parameters = null;
            Nodes parametersNode = menuNode.query("parameters/parameter");
            for(int i=0;i<parametersNode.size();i++) {
                Node parameter = parametersNode.get(i);
                if(parameters == null) {
                    parameters = new ArrayList<String>();
                }
                parameters.add(parameter.getValue());
            }
            ((MenuLevelCommand)level).setParameters(parameters);
        }else if(MenuLevelFolder.TYPE.equals(type)) {
            String id = menuNode.getAttributeValue("id");
            level = new MenuLevelFolder(id,getMenuName(menuNode.getChildElements("labels")), (List<MenuLevel>)null);
        }else if(MenuLevelImageFolder.TYPE.equals(type)) {
            String id = menuNode.getAttributeValue("id");
            level = new MenuLevelImageFolder(id,getMenuName(menuNode.getChildElements("labels")), (List<MenuLevel>)null);
        }else {
            String format = null;
            if(menuNode.getAttributeValue("format")!=null) {
                format = menuNode.getAttributeValue("format");
            }
            String playable = "true";
            if(menuNode.getAttributeValue("playable")!=null) {
                playable = menuNode.getAttributeValue("playable");
            }
            Long criteriaDepth = null;
            if(menuNode.getAttributeValue("criteriaDepth")!=null) {
                criteriaDepth = Long.valueOf(menuNode.getAttributeValue("criteriaDepth"));
            }
            level = new MenuLevelDynamic(type, getMenuName(menuNode.getChildElements("labels")), format, Boolean.valueOf(playable), criteriaDepth);
        }

        if(menuNode.getAttributeValue("weight")!=null) {
            level.setWeight(Integer.valueOf(menuNode.getAttributeValue("weight")));
        }
        return level;
    }

    /**
     * Add new default item format, this will be used of now specific format has been configured on a specific menu item
     *
     * @param menuType   Type of menu
     * @param objectType Type of item
     * @param format     The format to use, see {@link org.socialmusicdiscovery.server.support.format.TitleFormat} for more information
     */
    public void addDefaultItemFormat(MenuType menuType, String objectType, String format) {
        defaultValueConfigurationManager.setParameter(new ConfigurationParameterEntity(getClass().getName() + "." + menuType + ".formats."+objectType, ConfigurationParameter.Type.STRING,format));
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

    private void addDefaultFormat(MenuType menuType, MenuLevel level) {
        if (level instanceof MenuLevelDynamic && ((MenuLevelDynamic)level).getFormat() == null) {
            ((MenuLevelDynamic)level).setFormat(getDefaultItemFormat(menuType, level.getType()));
        }
        if(level.getChildLevels()!=null) {
            for (MenuLevel childLevel : level.getChildLevels()) {
                addDefaultFormat(menuType, childLevel);
            }
        }
    }
    /**
     * Add a new menu for the specified menu type
     *
     * @param menuType Menu type to add menu in
     * @param menu     Menu to add
     */
    public void addMenu(MenuType menuType, MenuLevel menu) {
        addDefaultFormat(menuType, menu);
        if(this.menus.get(menuType)!=null) {
            addToHierarchy(this.menus.get(menuType), Arrays.asList(menu));
        }else {
            this.menus.put(menuType, Arrays.asList(menu));
        }
    }

    private void addToHierarchy(List<MenuLevel> existingLevels, List<MenuLevel> newLevels) {
        for (MenuLevel newLevel : newLevels) {
            MenuLevel matchingLevel = null;
            for (MenuLevel existingLevel : existingLevels) {
                if(newLevel.getId().equals(existingLevel.getId()) &&
                        ((newLevel.getContext()==null && existingLevel.getContext()==null) ||
                         (newLevel.getContext()!=null && newLevel.getContext().equals(existingLevel.getContext())))) {

                    matchingLevel = existingLevel;
                    break;
                }
            }
            if(matchingLevel==null) {
                existingLevels.add(newLevel);
                sortMenuLevels(existingLevels);
            }else {
                if(matchingLevel.getChildLevels()==null && newLevel.getChildLevels()!=null) {
                    matchingLevel.setChildLevels(new ArrayList<MenuLevel>());
                }
                if(newLevel.getChildLevels()!=null) {
                    addToHierarchy(matchingLevel.getChildLevels(), newLevel.getChildLevels());
                }
            }
        }
    }

    /**
     * Remove a previously added menu
     *
     * @param menuType The menu type the menu was registered for
     * @param id       The id of the menu, see {@link org.socialmusicdiscovery.server.business.service.browse.MenuLevel#getId()}
     */
    public void removeMenu(MenuType menuType, String id) {
        this.menus.get(menuType).remove(id);
    }

    /**
     * Remove a previously added menu
     *
     * @param menuType The menu type the menu was registered for
     * @param context  The context the menu was registered in, see {@link org.socialmusicdiscovery.server.business.service.browse.MenuLevel#getContext()}
     * @param id       The id of the menu, see {@link org.socialmusicdiscovery.server.business.service.browse.MenuLevel#getId()}
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
    public List<MenuLevel> getAllMenus(MenuType menuType) {
        return getAllMenus(menuType, null);
    }

    /**
     * Get all menus for the specified menu type
     *
     * @param menuType The typ of menus to get
     * @param context The context which the menu items has to be related to
     * @return A sorted list of menus which are available for the specified item type
     */
    public List<MenuLevel> getAllMenus(MenuType menuType, String context) {
        List<MenuLevel> result = new ArrayList<MenuLevel>(this.menus.get(menuType).size());

        String baseContext = context;
        if (context != null && context.contains(".")) {
            baseContext = context.substring(0, context.indexOf("."));
        }
        for (MenuLevel level : this.menus.get(menuType)) {
            if((context==null && level.getContext()==null) ||
              (level.getContext()!=null &&
                      (level.getContext().equals(context) || level.getContext().equals(baseContext)))) {

                result.add(level);
            }
        }
        sortMenuLevels(result);
        return result;
    }

    private void sortMenuLevels(List<MenuLevel> levels) {
        Collections.sort(levels, new Comparator<MenuLevel>() {
            @Override
            public int compare(MenuLevel m1, MenuLevel m2) {
                int weight = m1.getWeight().compareTo(m2.getWeight());
                if (weight == 0) {
                    return m1.getDisplayName().compareTo(m2.getDisplayName());
                } else {
                    return weight;
                }
            }
        });
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
