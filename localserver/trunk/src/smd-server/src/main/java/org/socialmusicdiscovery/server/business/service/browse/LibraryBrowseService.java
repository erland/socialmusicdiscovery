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

    public LibraryBrowseService() {
        InjectHelper.injectMembers(this);
        configurationManager = new MergedConfigurationManager(new PersistentConfigurationManager());
    }

    protected Collection<MenuLevel> getMenus(String context) {
        return browseMenuManager.getAllMenus(BrowseMenuManager.MenuType.LIBRARY, context);
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

        String currentPath = parentPath;

        // Get all menus for current context type
        Collection<MenuLevel> menus = getMenus(currentType);
        Collection<MenuLevel> possibleLevels = menus;

        if (currentPath == null) {
            currentPath = "";
        }

        // Build criteria map
        Map<String, String> criteriaMap = new HashMap<String, String>();
        StringTokenizer tokens = new StringTokenizer(currentPath, "/");
        while (tokens.hasMoreElements()) {
            String token = tokens.nextToken();
            if (token.startsWith(MenuLevelFolder.TYPE+":")) {
                // We need to make folder items unique
                criteriaMap.put(token,null);
            }else if (token.startsWith(MenuLevelImageFolder.TYPE + ":")) {
                // We need to make folder items unique
                criteriaMap.put(token,null);
            }else if(token.startsWith(MenuLevelCommand.TYPE+":")) {
                // We need to make command items unique
                criteriaMap.put(token,null);
            }else if (token.contains(":")) {
                criteriaMap.put(token.substring(0, token.indexOf(":")), token.substring(token.indexOf(":") + 1));
            } else {
                criteriaMap.put(token, null);
            }
        }

        // Add context to criteria
        List<String> criterias = new ArrayList<String>();
        int criteriaOffset = 0;
        if (currentId != null) {
            criterias.add(currentId);
            criteriaOffset = 1;
        }

        Map<String, String> remainingCriteria = new HashMap<String, String>(criteriaMap);
        // Find the next menu level which we should return data for
        boolean found = true;
        while (found && possibleLevels != null) {
            found = false;
            for (MenuLevel level : possibleLevels) {
                if (remainingCriteria.containsKey(level.getId())) {
                    String value = remainingCriteria.get(level.getId());
                    if (value != null) {
                        criterias.add(level.getType() + ":" + value);
                    }
                    remainingCriteria.remove(level.getId());
                    possibleLevels = level.getChildLevels();
                    found = true;
                    break;
                }
            }
        }

        // Add context to criteria map
        if(currentId!=null && currentId.contains(":")) {
            criteriaMap.put(currentId.substring(0,currentId.indexOf(":")), currentId.substring(currentId.indexOf(":")+1));
        }

        // If a matching level was found which have childs
        if (possibleLevels != null) {
            for (MenuLevel requestedObjectType : possibleLevels) {
                if (maxItems != null && maxItems < 0) {
                    maxItems=0;
                }

                Result browseResult = new Result<Object>();

                // Static menus are just added as a single item
                if (!(requestedObjectType instanceof MenuLevelDynamic)) {
                    if ((firstItem == null || firstItem == 0) && (maxItems==null||maxItems>0)) {
                        ResultItem<Object> item = new ResultItem<Object>(null, requestedObjectType.getType(), requestedObjectType.getId(), requestedObjectType.getDisplayName(), requestedObjectType.isPlayable(), requestedObjectType.getChildLevels() == null);
                        if(requestedObjectType instanceof MenuLevelCommand && ((MenuLevelCommand)requestedObjectType).getParameters()!=null) {
                            List<String> parameters = new ArrayList<String>();
                            for (String name : ((MenuLevelCommand)requestedObjectType).getParameters()) {
                                String value = criteriaMap.get(name);
                                if(value!=null) {
                                    parameters.add(name+":"+value);
                                }
                            }
                            item.setParameters(parameters);
                        }
                        browseResult = new Result<Object>(1, new ArrayList<ResultItem<Object>>(Arrays.asList(item)));
                        if(counts) {
                            Map<String, Long> childCounters = new HashMap<String, Long>();
                            for (MenuLevel childLevel : requestedObjectType.getChildLevels()) {
                                if(!(childLevel instanceof MenuLevelDynamic)) {
                                    childCounters.put(childLevel.getId(), 1L);
                                }else {
                                    List<String> criteriasForThisLevel = new ArrayList<String>(criterias);

                                    String requestedMainObjectType = null;
                                    if (childLevel.getType().contains(".")) {
                                        requestedMainObjectType = childLevel.getType().substring(0, childLevel.getType().indexOf("."));
                                        criteriasForThisLevel.add(childLevel.getType());
                                    } else {
                                        requestedMainObjectType = childLevel.getType();
                                    }

                                    // Remove criterias according to criteria depth for selected menu item
                                    if (childLevel instanceof MenuLevelDynamic && ((MenuLevelDynamic)childLevel).getCriteriaDepth() != null) {
                                        while (criteriasForThisLevel.size() > ((MenuLevelDynamic)childLevel).getCriteriaDepth()) {
                                            criteriasForThisLevel.remove(0);
                                        }
                                    }

                                    // Lookup and call browse service
                                    BrowseService browseService = browseServiceManager.getBrowseService(requestedMainObjectType);
                                    Integer count = browseService.findChildrenCount(criteriasForThisLevel);
                                    childCounters.put(childLevel.getId(), count.longValue());
                                }
                            }
                            item.setChildItems(childCounters);
                        }
                    } else {
                        browseResult = new Result<Object>();
                        browseResult.setCount(1);
                    }

                // Dynamic menus retrieves their data using browse services
                } else {
                    List<String> criteriasForThisLevel = new ArrayList<String>(criterias);

                    // Detect type of object we want to retrieve
                    String requestedMainObjectType = null;
                    if (requestedObjectType.getType().contains(".")) {
                        requestedMainObjectType = requestedObjectType.getType().substring(0, requestedObjectType.getType().indexOf("."));
                        criteriasForThisLevel.add(requestedObjectType.getType());
                    } else {
                        requestedMainObjectType = requestedObjectType.getType();
                    }

                    // Remove criterias according to criteria depth for selected menu item
                    if (((MenuLevelDynamic)requestedObjectType).getCriteriaDepth() != null) {
                        while (criteriasForThisLevel.size() > ((MenuLevelDynamic)requestedObjectType).getCriteriaDepth()) {
                            criteriasForThisLevel.remove(0);
                        }
                    }

                    TitleFormat parser = null;
                    if(((MenuLevelDynamic)requestedObjectType).getFormat()!=null) {
                        parser = new TitleFormat(((MenuLevelDynamic)requestedObjectType).getFormat());
                    }

                    // Lookup and call browse service
                    BrowseService browseService = browseServiceManager.getBrowseService(requestedMainObjectType);
                    browseResult = browseService.findChildren(criteriasForThisLevel, new ArrayList<String>(), firstItem, maxItems, counts);

                    if (maxItems == null || maxItems >0) {
                        // Iterate through browse service result and create appropriate items
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
                            if(parser != null) {
                                item.setName(requestedObjectType.getDisplayName()+parser.format(item.getItem()));
                            }else {
                                item.setName(requestedObjectType.getDisplayName()+item.getItem().toString());
                            }
                            item.setPlayable(requestedObjectType.isPlayable());
                            item.setLeaf(requestedObjectType.getChildLevels() == null);

                            if (counts) {
                                Map<String, Long> childCounters = new HashMap<String, Long>();
                                if(requestedObjectType.getChildLevels()!=null) {
                                    for (MenuLevel childLevel : requestedObjectType.getChildLevels()) {
                                        if (item.getChildItems()!=null && item.getChildItems().containsKey(childLevel.getType())) {
                                            childCounters.put(childLevel.getType(), (Long) item.getChildItems().get(childLevel.getType()));
                                        }
                                    }
                                }
                                item.setChildItems(childCounters);
                            }
                        }
                    }else {
                        Integer count = browseResult.getCount();
                        browseResult = new Result<Object>();
                        browseResult.setCount(count);
                    }
                }

                // Decrease max items in case there are more menu levels to retrieve data for
                if (maxItems != null && browseResult.getItems().size()>0) {
                    maxItems = maxItems - browseResult.getItems().size();
                    if (maxItems < 0) {
                        maxItems = 0;
                    }
                }

                // Set offset to 0 in case there are more menu levels to retrieve data for
                if (firstItem != null && browseResult.getItems().size() > 0) {
                    firstItem = 0;
                }else if(firstItem != null) {
                    firstItem = firstItem - browseResult.getCount();
                    if(firstItem<0) {
                        firstItem = 0;
                    }
                }
                result.setCount(result.getCount() + browseResult.getCount());
                if (result.getAlphabetic() != null && browseResult.getAlphabetic() != null) {
                    result.setAlphabetic(result.getAlphabetic() && browseResult.getAlphabetic());
                } else {
                    result.setAlphabetic(null);
                }
                result.getItems().addAll(browseResult.getItems());
            }
        }

        fillContext(result, currentBaseType, currentId);
        return result;
    }

    private void fillContext(Result result, String currentBaseType, String currentId) {
        if (currentId != null && currentId.contains(":") && currentBaseType != null) {
            if(!currentBaseType.equals(MenuLevelFolder.TYPE) &&
                    !currentBaseType.equals(MenuLevelImageFolder.TYPE) &&
                    !currentBaseType.equals(MenuLevelCommand.TYPE)) {

                BrowseService browseService = browseServiceManager.getBrowseService(currentBaseType);
                if (browseService != null) {
                    ResultItem currentItem = browseService.findById(currentId.substring(currentId.indexOf(":") + 1));
                    if (currentItem != null) {
                        if (currentItem.getName() == null) {
                            String format = browseMenuManager.getDefaultItemFormat(BrowseMenuManager.MenuType.CONTEXT, currentItem.getType());
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
}

