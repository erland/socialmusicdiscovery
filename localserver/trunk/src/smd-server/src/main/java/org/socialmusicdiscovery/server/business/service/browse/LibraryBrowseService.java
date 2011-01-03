package org.socialmusicdiscovery.server.business.service.browse;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import java.util.*;

public class LibraryBrowseService {
    @Inject
    ObjectTypeBrowseService objectTypeBrowseService;

    private static class Menu {
        String name;
        String id;
        List<String> objectTypes;
        Menu(String id, String name, List<String> objectTypes) {
            this.id = id;
            this.name = name;
            this.objectTypes = objectTypes;
        }
    }
    private List<Menu> menus = new ArrayList<Menu>();

    public LibraryBrowseService() {
        InjectHelper.injectMembers(this);
        menus.add(new Menu("artists", "Artists", Arrays.asList("Artist", "Release", "Track")));
        menus.add(new Menu("artists.composers", "Composers", Arrays.asList("Artist.composer", "Release", "Track")));
        menus.add(new Menu("artists.conductors", "Conductors", Arrays.asList("Artist.conductor", "Release", "Track")));
        menus.add(new Menu("releases", "Releases", Arrays.asList("Release", "Track")));
        menus.add(new Menu("classifications.genres", "Genres", Arrays.asList("Classification.genre", "Artist", "Release", "Track")));
        menus.add(new Menu("classifications.styles", "Styles", Arrays.asList("Classification.style", "Artist", "Release", "Track")));
        menus.add(new Menu("classifications.moods","Moods",Arrays.asList("Classification.mood","Artist","Release","Track")));
    }

    public Result<Object> findChildren(String parentPath, Integer firstItem, Integer maxItems, Boolean counts) {
        Result<Object> result = new Result<Object>();
        if(parentPath==null) {
            Collection<ResultItem<Object>> items = new ArrayList<ResultItem<Object>>();
            result.setItems(items);
            List<Menu> menus = this.menus;
            int i=0;
            Map<String,Long> counters = null;
            if(counts) {
                counters = objectTypeBrowseService.findObjectTypes(new ArrayList<String>(),true);
            }
            for (Menu menu : menus) {
                if((firstItem==null || firstItem<=i) && (maxItems==null || maxItems>items.size())) {
                    if(counts) {
                        Map<String,Long> childCounters = new HashMap<String,Long>(1);
                        if(counters.get(menu.objectTypes.get(0))!=null) {
                            childCounters.put(menu.objectTypes.get(0),counters.get(menu.objectTypes.get(0)));
                        }
                        items.add(new ResultItem<Object>(menu.name,"Folder",menu.id,childCounters));
                    }else {
                        items.add(new ResultItem<Object>(menu.name,"Folder",menu.id));
                    }
                }
                i++;
            }
            result.setCount((long)items.size());
        }else {
            String currentPath = parentPath;
            Menu currentMenu = null;

            for (Menu menu : menus) {
                if(currentPath.equals(menu.id) || currentPath.contains("/") && currentPath.substring(0,currentPath.indexOf("/")).equals(menu.id)) {
                    currentMenu = menu;
                    currentPath = currentPath.substring(menu.id.length());
                    if(currentPath.startsWith(".")) {
                        currentPath = currentPath.substring(1);
                    }
                    break;
                }
            }

            if(currentMenu != null) {
                Map<String, String> criteriaMap = new HashMap<String, String>();
                StringTokenizer tokens = new StringTokenizer(currentPath,"/");
                while(tokens.hasMoreElements()) {
                    String token = tokens.nextToken();
                    if(token.contains(":")) {
                        criteriaMap.put(token.substring(0, token.indexOf(":")), token.substring(token.indexOf(":") + 1));
                    }else {
                        criteriaMap.put(token, null);
                    }
                }

                List<String> criterias = new ArrayList<String>();
                String requestedMainObjectType = null;
                String requestedObjectType = null;
                String nextObjectType = null;
                for (String objectType : currentMenu.objectTypes) {
                    String value = criteriaMap.get(objectType);
                    if(value != null) {
                        criterias.add(objectType+":"+value);
                    }else {
                        nextObjectType = currentMenu.objectTypes.get(criterias.size()+1);
                        requestedObjectType = objectType;
                        if(objectType.contains(".")) {
                            requestedMainObjectType=objectType.substring(0,objectType.indexOf("."));
                            criterias.add(objectType);
                        }else {
                            requestedMainObjectType=objectType;
                        }
                        break;
                    }
                }

                if(requestedMainObjectType!=null) {
                    BrowseService browseService = InjectHelper.instanceWithName(BrowseService.class,requestedMainObjectType);
                    Result browseResult = browseService.findChildren(criterias,new ArrayList<String>(),firstItem,maxItems,counts);
                    Collection<ResultItem> browseResultItems = browseResult.getItems();
                    for (ResultItem item : browseResultItems) {
                        String id = null;
                        if(item.getItem() instanceof SMDIdentity) {
                            id = requestedObjectType+":"+((SMDIdentity)item.getItem()).getId();
                        }else {
                            id = requestedObjectType+":"+item.getItem().toString();
                        }
                        item.setId(id);
                        item.setType(requestedMainObjectType);

                        if(counts) {
                            Map<String, Long> childCounters = new HashMap<String, Long>();
                            if(nextObjectType!=null && item.getChildItems().containsKey(nextObjectType)) {
                                childCounters.put(nextObjectType,(Long)item.getChildItems().get(nextObjectType));
                            }
                            item.setChildItems(childCounters);
                        }
                    }
                    result = browseResult;
                }
            }
        }
        return result;
    }
}
