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

package org.socialmusicdiscovery.server.api.query;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.TransactionManager;
import org.socialmusicdiscovery.server.business.model.core.PlayableElement;
import org.socialmusicdiscovery.server.business.model.core.TrackEntity;
import org.socialmusicdiscovery.server.business.service.browse.*;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * Provides functionality for browsing the library
 */
@Path("/browse")
public class BrowseFacade {
    /** The transaction manager used by this facade */
    @Inject
    protected TransactionManager transactionManager;

    @Inject
    BrowseServiceManager browseServiceManager;

    @Inject
    BrowseMenuManager browseMenuManager;

    @Inject
    ObjectTypeBrowseService objectTypeBrowseService;

    public BrowseFacade() {
        InjectHelper.injectMembers(this);
    }

    /**
     * Browse for objects of a specific type
     *
     * @param object       The type of object to browse for
     * @param criteriaList Criteria which the objects have to match, multiple criteria can be specified as separate request parameters
     * @param offset       Offset of the first item to get, this is used to get the result in smaller chunks
     * @param size         Number of items to get
     * @param childs       true if child counters should be provided
     * @return A list of matching objects
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{object}")
    public Result browseChildren(@PathParam("object") String object, @QueryParam("criteria") List<String> criteriaList, @QueryParam("offset") Integer offset, @QueryParam("size") Integer size, @QueryParam("childs") Boolean childs, @QueryParam("itemInfo") Boolean itemInfo) {
        if (size != null && offset == null) {
            offset = 0;
        }

        if (object.contains(".")) {
            criteriaList = new ArrayList<String>(criteriaList);
            criteriaList.add(0, object);
            object = object.substring(0, object.indexOf("."));
        }

        BrowseService browseService = browseServiceManager.getBrowseService(object);
        org.socialmusicdiscovery.server.business.service.browse.Result result = new CopyHelper().detachedCopy(browseService.findChildren(criteriaList, new ArrayList<String>(), offset, size, childs));

        List<ItemResult.Item> genericResultItems = new ArrayList<ItemResult.Item>(result.getItems().size());
        for (Object o : result.getItems()) {
            ResultItem resultItem = (ResultItem) o;
            Object item = null;
            if (itemInfo != null && itemInfo) {
                item = resultItem.getItem();
            }
            ItemResult.ItemImage image = null;
            if(resultItem.getImage()!=null) {
                image = new ItemResult.ItemImage(resultItem.getImage().getProviderId(), resultItem.getImage().getProviderImageId(), resultItem.getImage().getUrl());
            }
            if (resultItem.getChildItems() != null) {
                genericResultItems.add(new ItemResult.Item(item, resultItem.getType(), resultItem.getId(), resultItem.getName(), resultItem.getSortKey(), image, resultItem.getPlayable(), getPlayableElementsURL(criteriaList, resultItem.getId()), null, new HashMap<String, Long>(resultItem.getChildItems())));
            } else {
                genericResultItems.add(new ItemResult.Item(item, resultItem.getType(), resultItem.getId(), resultItem.getName(), resultItem.getSortKey(), image, resultItem.getPlayable(), getPlayableElementsURL(criteriaList, resultItem.getId()), null, resultItem.getLeaf()));
            }
        }

        if (size != null) {
            return new ItemResult(genericResultItems, result.getAlphabetic(), result.getCount(), offset.intValue(), result.getItems().size());
        } else {
            return new ItemResult(genericResultItems, result.getAlphabetic(), result.getCount(), 0, result.getItems().size());
        }
    }

    /**
     * Execute the specified command with the specified object as parameter
     *
     * @param command The command to execute
     * @param object  The object identity to pass as parameter
     * @return The result of the executed command
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/command/{command}/{object:.*}")
    public CommandResult executeCommand(@PathParam("command") String command, @PathParam("object") String object) {
        try {
            transactionManager.begin();

            List<String> objects = new ArrayList<String>();
            if (object!=null) {
                objects.addAll(Arrays.asList(object.split("/")));
            }
            Command cmd = browseMenuManager.getCommand(command);
            org.socialmusicdiscovery.server.business.service.browse.CommandResult result;
            if (cmd != null) {
                result = cmd.executeCommand(objects);
                if (result == null) {
                    result = new org.socialmusicdiscovery.server.business.service.browse.CommandResult(false, "Error executing command \"" + command + "\" on object " + object);
                }
            } else {
                result = new org.socialmusicdiscovery.server.business.service.browse.CommandResult(false, "Error executing command \"" + command + "\" on object " + object);
            }

            return new CommandResult(result.getSuccess(), result.getMessage());
        }catch (RuntimeException e) {
            transactionManager.setRollbackOnly();
            throw e;
        }finally {
            transactionManager.end();
        }
    }

    /**
     * Retrieve playable elements matching the specified criterias
     *
     * @param criteriaList Critieras to use when searching
     * @param offset       First item to return
     * @param size         Maximum number of items to return
     * @return A list of matching playable elements
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/PlayableElement")
    public PlayableElementResult browseChildren(@QueryParam("criteria") List<String> criteriaList, @QueryParam("offset") Integer offset, @QueryParam("size") Integer size) {
        if (size != null && offset == null) {
            offset = 0;
        }

        TrackBrowseService browseService = browseServiceManager.getBrowseService("Track");
        OnlinePlayableElementService onlinePlayableElementService = null;
        String trackId = null;

        // We want to start from the right, because that's where the most specific criterias typically are
        List<String> reversedCriteraList = new ArrayList<String>(criteriaList);
        Collections.reverse(reversedCriteraList);
        for (String criteria : reversedCriteraList) {
            // Track already points to a unique playable element, so if we got a Track we can stop searching
            if (criteria.startsWith("Track:")) {
                trackId = criteria.substring(6);
                break;
            }
            // Check if criteria is a online criteria which support playable elements
            // In this case we want to play the online element and won't look it up in the database
            String objectType = criteria;
            if (objectType.contains(":")) {
                objectType = objectType.substring(0, objectType.indexOf(":"));
            }
            if (browseServiceManager.existsOnlinePlayableElementService(objectType)) {
                onlinePlayableElementService = browseServiceManager.getOnlinePlayableElementService(objectType);
                break;
            }
        }

        List<OnlinePlayableElement> genericResultItems = new ArrayList<OnlinePlayableElement>();
        if (onlinePlayableElementService == null) {
            // Lookup playable elements from database if no online criteria was found
            org.socialmusicdiscovery.server.business.service.browse.Result<TrackEntity> result = null;
            if (trackId != null) {
                // Special case for track since it's not supported to look for Track objects in TrackBrowseService
                ResultItem<TrackEntity> track = browseService.findById(trackId);
                if (track != null) {
                    result = new CopyHelper().detachedCopy(new org.socialmusicdiscovery.server.business.service.browse.Result<TrackEntity>(1, new ArrayList<ResultItem<TrackEntity>>(Arrays.asList(track))));
                } else {
                    result = new org.socialmusicdiscovery.server.business.service.browse.Result<TrackEntity>();
                }
            } else {
                // Lookup track objects matching the search criteria
                result = new CopyHelper().detachedCopy(browseService.findChildren(criteriaList, new ArrayList<String>(), offset, size, false));
            }

            // Extract the first playable element for each found track object
            for (ResultItem<TrackEntity> resultItem : result.getItems()) {
                PlayableElement playableElement = resultItem.getItem().getPlayableElements().iterator().next();
                if (playableElement != null) {
                    genericResultItems.add(new OnlinePlayableElement(playableElement));
                }
            }

            if (size != null) {
                return new PlayableElementResult(genericResultItems, result.getCount(), offset.intValue(), result.getItems().size());
            } else {
                return new PlayableElementResult(genericResultItems, result.getCount(), 0, result.getItems().size());
            }
        } else {
            // Retrieve playable elements from service supporting the specified criteria type
            genericResultItems = onlinePlayableElementService.find(criteriaList);
            return new PlayableElementResult(genericResultItems, genericResultItems.size(), 0, genericResultItems.size());
        }
    }

    /**
     * Browse for object types that's available under a specific object which has been found using a list of search criterias.
     * The counters returned only includes the objects that matches the specified search criterias.
     *
     * @param criteriaList Criteria which the parent object and sub items have to match, multiple criteria can be specified as separate request parameters
     * @param counters     true if number of items per object type should be provided
     * @return A list of matching object types and optionally counters
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Result.Child> browseObjectTypes(@QueryParam("criteria") List<String> criteriaList, @QueryParam("counters") Boolean counters) {
        Map<String, Long> queryResult = objectTypeBrowseService.findObjectTypes(criteriaList, counters);
        List<Result.Child> result = new ArrayList<Result.Child>(queryResult.size());
        for (Map.Entry<String, Long> entry : queryResult.entrySet()) {
            result.add(new Result.Child(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    /**
     * Browse objects by using the predefined menu structure, starting at the top
     *
     * @param offset Offset of the first item to get, this is used to get the result in smaller chunks
     * @param size   Number of items to get
     * @param childs true if child counters should be provided
     * @return A list of matching objects
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/library")
    public Result browseLibrary(@QueryParam("offset") Integer offset, @QueryParam("size") Integer size, @QueryParam("childs") Boolean childs, @QueryParam("itemInfo") Boolean itemInfo) {
        return browseLibrary(null, offset, size, childs, itemInfo);
    }

    /**
     * Browse objects by using the predefined menu structure, starting at the the parent object specified as input
     *
     * @param objectId The object to start the browsing from, this needs to be the full path to this object
     * @param offset   Offset of the first item to get, this is used to get the result in smaller chunks
     * @param size     Number of items to get
     * @param childs   true if child counters should be provided
     * @return A list of matching objects
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/library/{object:.*}")
    public Result browseLibrary(@PathParam("object") String objectId, @QueryParam("offset") Integer offset, @QueryParam("size") Integer size, @QueryParam("childs") Boolean childs, @QueryParam("itemInfo") Boolean itemInfo) {
        if (size != null && offset == null) {
            offset = 0;
        }

        LibraryBrowseService browseService = InjectHelper.instance(LibraryBrowseService.class);
        org.socialmusicdiscovery.server.business.service.browse.Result result = new CopyHelper().detachedCopy(browseService.findChildren(objectId, offset, size, childs));

        List<String> parentObjects = new ArrayList<String>();
        if (objectId != null) {
            parentObjects = Arrays.asList(objectId.split("/"));
        }
        List<ItemResult.Item> genericResultItems = new ArrayList<ItemResult.Item>(result.getItems().size());
        for (Object o : result.getItems()) {
            ResultItem resultItem = (ResultItem) o;
            Object item = null;
            if (itemInfo != null && itemInfo) {
                item = resultItem.getItem();
            }
            ItemResult.ItemImage image = null;
            if(resultItem.getImage()!=null) {
                image = new ItemResult.ItemImage(resultItem.getImage().getProviderId(), resultItem.getImage().getProviderImageId(), resultItem.getImage().getUrl());
            }
            if (resultItem.getChildItems() != null) {
                genericResultItems.add(new ItemResult.Item(item, resultItem.getType(), resultItem.getId(), resultItem.getName(), resultItem.getSortKey(), image, resultItem.getPlayable(), getPlayableElementsURL(null, resultItem.getId()), getCommandURL(resultItem), new HashMap<String, Long>(resultItem.getChildItems())));
            } else {
                genericResultItems.add(new ItemResult.Item(item, resultItem.getType(), resultItem.getId(), resultItem.getName(), resultItem.getSortKey(), image, resultItem.getPlayable(), getPlayableElementsURL(null, resultItem.getId()), getCommandURL(resultItem), resultItem.getLeaf()));
            }
        }

        if (size != null) {
            return new ItemResult(genericResultItems, result.getAlphabetic(), getPlayableElementsURL(parentObjects, null), result.getCount(), offset, result.getItems().size());
        } else {
            return new ItemResult(genericResultItems, result.getAlphabetic(), getPlayableElementsURL(parentObjects, null), result.getCount(), 0, result.getItems().size());
        }
    }

    private String getPlayableElementsURL(List<String> parentHierarchy, String objectId) {
        StringBuffer sb = new StringBuffer();
        if (parentHierarchy != null) {
            sb.append("/browse/PlayableElement?");
            for (String object : parentHierarchy) {
                if (!object.startsWith(MenuLevelFolder.TYPE+":") &&
                        !object.startsWith(MenuLevelImageFolder.TYPE+":") &&
                        object.contains(":")) {
                    sb.append("&criteria=");
                    sb.append(object);
                }
            }
        }
        if (objectId != null) {
            sb.append("&criteria=");
            sb.append(objectId);
        }
        return sb.toString();
    }

    private String getCommandURL(ResultItem item) {
        if (item.getType().equals(Command.class.getSimpleName())) {
            String id = item.getId();
            if(id.startsWith(item.getType()+":")) {
                id = id.substring((item.getType()+":").length());
            }
            String parameters = "";
            if(item.getParameters()!=null) {
                for (Object value : item.getParameters()) {
                    parameters+="/"+value;
                }
            }

            return "/browse/command/" + id +parameters;
        }
        return null;
    }

    /**
     * Browse context of a specified objects by using the predefined menu structure, starting at the the parent object specified as input
     *
     * @param objectId The object to start the browsing from, this needs to be the full path to this object
     * @param offset   Offset of the first item to get, this is used to get the result in smaller chunks
     * @param size     Number of items to get
     * @param childs   true if child counters should be provided
     * @return A list of matching objects
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/context/{object:.*}")
    public Result browseContext(@PathParam("object") String objectId, @QueryParam("offset") Integer offset, @QueryParam("size") Integer size, @QueryParam("childs") Boolean childs, @QueryParam("itemInfo") Boolean itemInfo) {
        if (size != null && offset == null) {
            offset = 0;
        }

        ContextBrowseService browseService = InjectHelper.instance(ContextBrowseService.class);
        org.socialmusicdiscovery.server.business.service.browse.Result result = new CopyHelper().detachedCopy(browseService.findChildren(objectId, offset, size, childs));

        List<String> parentObjects = new ArrayList<String>(Arrays.asList(objectId.split("/")));
        parentObjects.remove(0);

        List<ItemResult.Item> genericResultItems = new ArrayList<ItemResult.Item>(result.getItems().size());
        for (Object o : result.getItems()) {
            ResultItem resultItem = (ResultItem) o;
            Object item = null;
            if (itemInfo != null && itemInfo) {
                item = resultItem.getItem();
            }
            ItemResult.ItemImage image = null;
            if(resultItem.getImage()!=null) {
                image = new ItemResult.ItemImage(resultItem.getImage().getProviderId(), resultItem.getImage().getProviderImageId(), resultItem.getImage().getUrl());
            }
            if (resultItem.getChildItems() != null) {
                genericResultItems.add(new ItemResult.Item(item, resultItem.getType(), resultItem.getId(), resultItem.getName(), resultItem.getSortKey(), image, resultItem.getPlayable(), getPlayableElementsURL(null, resultItem.getId()), getCommandURL(resultItem), new HashMap<String, Long>(resultItem.getChildItems())));
            } else {
                genericResultItems.add(new ItemResult.Item(item, resultItem.getType(), resultItem.getId(), resultItem.getName(), resultItem.getSortKey(), image, resultItem.getPlayable(), getPlayableElementsURL(null, resultItem.getId()), getCommandURL(resultItem), resultItem.getLeaf()));
            }
        }
        ItemResult.Item context = null;
        if (result.getContext() != null) {
            Object item = null;
            if (itemInfo != null && itemInfo) {
                item = result.getContext().getItem();
            }
            ItemResult.ItemImage image = null;
            if(result.getContext().getImage()!=null) {
                image = new ItemResult.ItemImage(result.getContext().getImage().getProviderId(), result.getContext().getImage().getProviderImageId(), result.getContext().getImage().getUrl());
            }
            if (result.getItems().size() > 0) {
                context = new ItemResult.Item(item, result.getContext().getType(), result.getContext().getId(), result.getContext().getName(), result.getContext().getSortKey(), image, result.getContext().getPlayable(), getPlayableElementsURL(parentObjects, null), null, false);
            } else {
                context = new ItemResult.Item(item, result.getContext().getType(), result.getContext().getId(), result.getContext().getName(), result.getContext().getSortKey(), image, result.getContext().getPlayable(), getPlayableElementsURL(parentObjects, null), null, true);
            }
        }
        if (size != null) {
            return new ContextItemResult(context, genericResultItems, result.getAlphabetic(), getPlayableElementsURL(parentObjects, null), result.getCount(), offset.intValue(), result.getItems().size());
        } else {
            return new ContextItemResult(context, genericResultItems, result.getAlphabetic(), getPlayableElementsURL(parentObjects, null), result.getCount(), 0, result.getItems().size());
        }
    }
}
