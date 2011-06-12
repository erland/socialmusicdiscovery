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
import org.socialmusicdiscovery.server.business.logic.config.ConfigurationManager;
import org.socialmusicdiscovery.server.business.logic.config.MappedConfigurationContext;
import org.socialmusicdiscovery.server.business.logic.config.MergedConfigurationManager;
import org.socialmusicdiscovery.server.business.logic.config.PersistentConfigurationManager;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.support.format.TitleFormat;

import java.util.*;

public class LibraryBrowseService {
    @Inject
    BrowseServiceManager browseServiceManager;

    @Inject
    BrowseMenuManager browseMenuManager;

    @Inject
    ObjectTypeBrowseService objectTypeBrowseService;

    private ConfigurationManager configurationManager;

    private List<Menu> menus = null;

    public LibraryBrowseService() {
        InjectHelper.injectMembers(this);
        configurationManager = new MergedConfigurationManager(new PersistentConfigurationManager());
    }

    protected Collection<Menu> getMenus() {
        return browseMenuManager.getAllMenus(BrowseMenuManager.MenuType.LIBRARY);
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
        return findChildren(null, parentPath, firstItem, maxItems, counts);
    }

    protected Result<Object> findChildren(String currentId, String parentPath, Integer firstItem, Integer maxItems, Boolean counts) {
        Result<Object> result = new Result<Object>();
        if (counts == null) {
            counts = false;
        }

        String currentType = null;
        if (currentId != null && currentId.contains(":")) {
            currentType = currentId.substring(0, currentId.indexOf(":"));
        }
        String currentBaseType = currentType;
        if (currentBaseType != null && currentBaseType.contains(".")) {
            currentBaseType = currentBaseType.substring(0, currentBaseType.indexOf("."));
        }

        if (parentPath == null) {
            List<ResultItem<Object>> items = new ArrayList<ResultItem<Object>>();
            result.setItems(items);
            Collection<Menu> menus = getMenus();
            int i = 0;
            Map<String, Long> counters = null;
            if (counts) {
                counters = objectTypeBrowseService.findObjectTypes(new ArrayList<String>(), true);
            }
            for (Menu menu : menus) {
                if (menu.getContext() != null && (currentType == null || (!currentType.equals(menu.getContext()) && !currentBaseType.equals(menu.getContext())))) {
                    continue;
                }
                if ((firstItem == null || firstItem <= i) && (maxItems == null || maxItems > items.size())) {
                    MenuLevel firstLevel = menu.getHierarchy().iterator().next();
                    if (counts && !firstLevel.getType().equals(CommandObject.class.getSimpleName())) {
                        Map<String, Long> childCounters = new HashMap<String, Long>(1);
                        if (counters.get(menu.getHierarchy().get(0).getType()) != null) {
                            childCounters.put(menu.getHierarchy().get(0).getType(), counters.get(menu.getHierarchy().get(0).getType()));
                        }
                        items.add(new ResultItem<Object>(menu.getName(), "Folder", menu.getId(), menu.getName(), false, childCounters));
                    } else {
                        if (firstLevel.getType().equals(CommandObject.class.getSimpleName())) {
                            items.add(new ResultItem<Object>(menu.getName(), firstLevel.getType(), menu.getId(), menu.getName(), false, false));
                        } else {
                            items.add(new ResultItem<Object>(menu.getName(), "Folder", menu.getId(), menu.getName(), false, false));
                        }
                    }
                }
                i++;
            }
            result.setCount((long) i);
        } else {
            String currentPath = parentPath;
            Menu currentMenu = null;

            for (Menu menu : getMenus()) {
                if (menu.getContext() != null && (currentType == null || (!currentType.equals(menu.getContext()) && !currentBaseType.equals(menu.getContext())))) {
                    continue;
                }
                if (currentPath.equals(menu.getId()) || currentPath.contains("/") && currentPath.substring(0, currentPath.indexOf("/")).equals(menu.getId())) {
                    currentMenu = menu;
                    currentPath = currentPath.substring(menu.getId().length());
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
                int criteriaOffset = 0;
                if (currentId != null) {
                    criterias.add(currentId);
                    criteriaOffset = 1;
                }

                String requestedMainObjectType = null;
                MenuLevel requestedObjectType = null;
                String nextObjectType = null;
                for (MenuLevel objectType : currentMenu.getHierarchy()) {
                    String value = criteriaMap.get(objectType.getType());
                    if (value != null) {
                        criterias.add(objectType.getType() + ":" + value);
                    } else {
                        if (currentMenu.getHierarchy().size() > criterias.size() + 1 - criteriaOffset) {
                            nextObjectType = currentMenu.getHierarchy().get(criterias.size() + 1 - criteriaOffset).getType();
                        }
                        requestedObjectType = objectType;
                        if (objectType.getType().contains(".")) {
                            requestedMainObjectType = objectType.getType().substring(0, objectType.getType().indexOf("."));
                            criterias.add(objectType.getType());
                        } else {
                            requestedMainObjectType = objectType.getType();
                        }
                        break;
                    }
                }

                if (requestedMainObjectType != null) {
                    if (requestedObjectType.getCriteriaDepth() != null) {
                        while (criterias.size() > requestedObjectType.getCriteriaDepth()) {
                            criterias.remove(0);
                        }
                    }
                    TitleFormat parser = new TitleFormat(requestedObjectType.getFormat());

                    BrowseService browseService = browseServiceManager.getBrowseService(requestedMainObjectType);
                    Result browseResult = browseService.findChildren(criterias, new ArrayList<String>(), firstItem, maxItems, counts);
                    Collection<ResultItem> browseResultItems = browseResult.getItems();
                    for (ResultItem item : browseResultItems) {
                        String id;
                        if (item.getItem() instanceof SMDIdentity) {
                            id = requestedObjectType.getType() + ":" + ((SMDIdentity) item.getItem()).getId();
                        } else {
                            id = requestedObjectType.getType() + ":" + item.getItem().toString();
                        }
                        item.setId(id);
                        item.setType(requestedMainObjectType);
                        item.setName(parser.format(item.getItem()));
                        item.setPlayable(requestedObjectType.getPlayable());
                        item.setLeaf(nextObjectType == null);

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
        fillContext(result, currentBaseType, currentId);
        return result;
    }

    private void fillContext(Result result, String currentBaseType, String currentId) {
        if (currentId != null && currentId.contains(":") && currentBaseType != null) {
            BrowseService browseService = browseServiceManager.getBrowseService(currentBaseType);
            if (browseService != null) {
                ResultItem currentItem = browseService.findById(currentId.substring(currentId.indexOf(":") + 1));
                if (currentItem != null) {
                    if (currentItem.getName() == null) {
                        MappedConfigurationContext config = new MappedConfigurationContext(getClass().getName() + ".formats.", configurationManager);
                        String format = config.getStringParameter(currentItem.getType());
                        if (format != null) {
                            currentItem.setName(new TitleFormat(format).format(currentItem.getItem()));
                        }
                    }
                    result.setContext(currentItem);
                }
            }
        }
    }
}

