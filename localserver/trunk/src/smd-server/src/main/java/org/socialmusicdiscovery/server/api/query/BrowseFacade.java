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
import org.socialmusicdiscovery.server.business.service.browse.BrowseService;
import org.socialmusicdiscovery.server.business.service.browse.LibraryBrowseService;
import org.socialmusicdiscovery.server.business.service.browse.ObjectTypeBrowseService;
import org.socialmusicdiscovery.server.business.service.browse.ResultItem;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * Provides functionality for browsing the library
 */
@Path("/browse")
public class BrowseFacade {
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
    public Result browseChildren(@PathParam("object") String object, @QueryParam("criteria") List<String> criteriaList, @QueryParam("offset") Integer offset, @QueryParam("size") Integer size, @QueryParam("childs") Boolean childs) {
        if (size != null && offset == null) {
            offset = 0;
        }

        if (object.contains(".")) {
            criteriaList = new ArrayList<String>(criteriaList);
            criteriaList.add(0, object);
            object = object.substring(0, object.indexOf("."));
        }

        BrowseService browseService = InjectHelper.instanceWithName(BrowseService.class, object);
        org.socialmusicdiscovery.server.business.service.browse.Result result = new CopyHelper().detachedCopy(browseService.findChildren(criteriaList, new ArrayList<String>(), offset, size, childs));

        List<Result.ResultItem> genericResultItems = new ArrayList<Result.ResultItem>(result.getItems().size());
        Iterator<ResultItem> itemIterator = result.getItems().iterator();
        while (itemIterator.hasNext()) {
            ResultItem resultItem = itemIterator.next();
            if (resultItem.getChildItems() != null) {
                genericResultItems.add(new Result.ResultItem(resultItem.getItem(), new HashMap<String, Long>(resultItem.getChildItems())));
            } else {
                genericResultItems.add(new Result.ResultItem(resultItem.getItem()));
            }
        }

        if (size != null) {
            return new Result(genericResultItems, result.getCount(), offset.longValue(), (long) result.getItems().size());
        } else {
            return new Result(genericResultItems, result.getCount(), 0L, (long) result.getItems().size());
        }
    }

    /**
     * Browse for object types that's available under a specific object which has been found using a list of search criterias.
     * The counters returned only includes the objects that matches the specified search criterias.
     *
     * @param criteriaList Criteria which the parent object and sub items have to match, multiple criteria can be specified as separate request parameters
     * @param counters    true if number of items per object type should be provided
     * @return A list of matching object types and optionally counters
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Result.Child> browseObjectTypes(@QueryParam("criteria") List<String> criteriaList, @QueryParam("counters") Boolean counters) {
        Map<String,Long> queryResult = objectTypeBrowseService.findObjectTypes(criteriaList, counters);
        List<Result.Child> result = new ArrayList<Result.Child>(queryResult.size());
        for (Map.Entry<String,Long> entry : queryResult.entrySet()) {
            result.add(new Result.Child(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    /**
     * Browse objects by using the predefined menu structure, starting at the top
     *
     * @param offset       Offset of the first item to get, this is used to get the result in smaller chunks
     * @param size         Number of items to get
     * @param childs       true if child counters should be provided
     * @return A list of matching objects
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/library")
    public Result browseLibrary(@QueryParam("offset") Integer offset, @QueryParam("size") Integer size, @QueryParam("childs") Boolean childs) {
        return browseLibrary(null, offset, size, childs);
    }

    /**
     * Browse objects by using the predefined menu structure, starting at the the parent object specified as input
     *
     * @param objectId   The object to start the browsing from, this needs to be the full path to this object
     * @param offset       Offset of the first item to get, this is used to get the result in smaller chunks
     * @param size         Number of items to get
     * @param childs       true if child counters should be provided
     * @return A list of matching objects
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/library/{object:.*}")
    public Result browseLibrary(@PathParam("object") String objectId, @QueryParam("offset") Integer offset, @QueryParam("size") Integer size, @QueryParam("childs") Boolean childs) {
        if (size != null && offset == null) {
            offset = 0;
        }

        LibraryBrowseService browseService = new LibraryBrowseService();
        org.socialmusicdiscovery.server.business.service.browse.Result result = new CopyHelper().detachedCopy(browseService.findChildren(objectId, offset, size, childs));

        List<Result.ResultItem> genericResultItems = new ArrayList<Result.ResultItem>(result.getItems().size());
        Iterator<ResultItem> itemIterator = result.getItems().iterator();
        while (itemIterator.hasNext()) {
            ResultItem resultItem = itemIterator.next();
            if (resultItem.getChildItems() != null) {
                genericResultItems.add(new Result.ResultItem(resultItem.getItem(), resultItem.getType(), resultItem.getId(), resultItem.getName(), new HashMap<String, Long>(resultItem.getChildItems())));
            } else {
                genericResultItems.add(new Result.ResultItem(resultItem.getItem(), resultItem.getType(), resultItem.getId(), resultItem.getName()));
            }
        }

        if (size != null) {
            return new Result(genericResultItems, result.getCount(), offset.longValue(), (long) result.getItems().size());
        } else {
            return new Result(genericResultItems, result.getCount(), 0L, (long) result.getItems().size());
        }
    }
}
