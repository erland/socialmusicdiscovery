package org.socialmusicdiscovery.server.business.service.browse;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.support.format.TitleFormat;

import java.util.*;

public class LibraryBrowseService {
    @Inject
    ObjectTypeBrowseService objectTypeBrowseService;

    private static class MenuLevel {
        String type;
        String format;

        MenuLevel(String type, String format) {
            this.type = type;
            this.format = format;
        }
    }

    private static class Menu {
        String name;
        String id;
        List<MenuLevel> hierarchy;

        Menu(String id, String name, List<MenuLevel> hierarchy) {
            this.id = id;
            this.name = name;
            this.hierarchy = hierarchy;
        }
    }

    private List<Menu> menus = new ArrayList<Menu>();

    public LibraryBrowseService() {
        InjectHelper.injectMembers(this);
        menus.add(new Menu("artists", "Artists", Arrays.asList(
                new MenuLevel("Artist", "%object.name"),
                new MenuLevel("Release", "%object.name"),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.work.parent.name||[%object.recording.work.parent,: ]||%object.recording.work.name"))));
        menus.add(new Menu("artists.composers", "Composers", Arrays.asList(
                new MenuLevel("Artist.composer", "%object.name"),
                new MenuLevel("Release", "%object.name"),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.work.parent.name||[%object.recording.work.parent,: ]||%object.recording.work.name"))));
        menus.add(new Menu("artists.conductors", "Conductors", Arrays.asList(
                new MenuLevel("Artist.conductor", "%object.name"),
                new MenuLevel("Release", "%object.name"),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.work.parent.name||[%object.recording.work.parent,: ]||%object.recording.work.name"))));
        menus.add(new Menu("releases", "Releases", Arrays.asList(
                new MenuLevel("Release", "%object.name"),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.work.parent.name||[%object.recording.work.parent,: ]||%object.recording.work.name"))));
        menus.add(new Menu("classifications.genres", "Genres", Arrays.asList(
                new MenuLevel("Classification.genre", "%object.name"),
                new MenuLevel("Artist", "%object.name"),
                new MenuLevel("Release", "%object.name"),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.work.parent.name||[%object.recording.work.parent,: ]||%object.recording.work.name"))));
        menus.add(new Menu("classifications.styles", "Styles", Arrays.asList(
                new MenuLevel("Classification.style", "%object.name"),
                new MenuLevel("Artist", "%object.name"),
                new MenuLevel("Release", "%object.name"),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.work.parent.name||[%object.recording.work.parent,: ]||%object.recording.work.name"))));
        menus.add(new Menu("classifications.moods", "Moods", Arrays.asList(
                new MenuLevel("Classification.mood", "%object.name"),
                new MenuLevel("Artist", "%object.name"),
                new MenuLevel("Release", "%object.name"),
                new MenuLevel("Track", "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.work.parent.name||[%object.recording.work.parent,: ]||%object.recording.work.name"))));
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
        Result<Object> result = new Result<Object>();
        if (counts == null) {
            counts = false;
        }
        if (parentPath == null) {
            Collection<ResultItem<Object>> items = new ArrayList<ResultItem<Object>>();
            result.setItems(items);
            List<Menu> menus = this.menus;
            int i = 0;
            Map<String, Long> counters = null;
            if (counts) {
                counters = objectTypeBrowseService.findObjectTypes(new ArrayList<String>(), true);
            }
            for (Menu menu : menus) {
                if ((firstItem == null || firstItem <= i) && (maxItems == null || maxItems > items.size())) {
                    if (counts) {
                        Map<String, Long> childCounters = new HashMap<String, Long>(1);
                        if (counters.get(menu.hierarchy.get(0).type) != null) {
                            childCounters.put(menu.hierarchy.get(0).type, counters.get(menu.hierarchy.get(0).type));
                        }
                        items.add(new ResultItem<Object>(menu.name, "Folder", menu.id, menu.name, childCounters));
                    } else {
                        items.add(new ResultItem<Object>(menu.name, "Folder", menu.id, menu.name));
                    }
                }
                i++;
            }
            result.setCount((long) menus.size());
        } else {
            String currentPath = parentPath;
            Menu currentMenu = null;

            for (Menu menu : menus) {
                if (currentPath.equals(menu.id) || currentPath.contains("/") && currentPath.substring(0, currentPath.indexOf("/")).equals(menu.id)) {
                    currentMenu = menu;
                    currentPath = currentPath.substring(menu.id.length());
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
                String requestedMainObjectType = null;
                MenuLevel requestedObjectType = null;
                String nextObjectType = null;
                for (MenuLevel objectType : currentMenu.hierarchy) {
                    String value = criteriaMap.get(objectType.type);
                    if (value != null) {
                        criterias.add(objectType.type + ":" + value);
                    } else {
                        if (currentMenu.hierarchy.size() > criterias.size() + 1) {
                            nextObjectType = currentMenu.hierarchy.get(criterias.size() + 1).type;
                        }
                        requestedObjectType = objectType;
                        if (objectType.type.contains(".")) {
                            requestedMainObjectType = objectType.type.substring(0, objectType.type.indexOf("."));
                            criterias.add(objectType.type);
                        } else {
                            requestedMainObjectType = objectType.type;
                        }
                        break;
                    }
                }

                if (requestedMainObjectType != null) {
                    TitleFormat parser = new TitleFormat(requestedObjectType.format);

                    BrowseService browseService = InjectHelper.instanceWithName(BrowseService.class, requestedMainObjectType);
                    Result browseResult = browseService.findChildren(criterias, new ArrayList<String>(), firstItem, maxItems, counts);
                    Collection<ResultItem> browseResultItems = browseResult.getItems();
                    for (ResultItem item : browseResultItems) {
                        String id = null;
                        if (item.getItem() instanceof SMDIdentity) {
                            id = requestedObjectType.type + ":" + ((SMDIdentity) item.getItem()).getId();
                        } else {
                            id = requestedObjectType.type + ":" + item.getItem().toString();
                        }
                        item.setId(id);
                        item.setType(requestedMainObjectType);
                        item.setName(parser.format(item.getItem()));

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
        return result;
    }
}

