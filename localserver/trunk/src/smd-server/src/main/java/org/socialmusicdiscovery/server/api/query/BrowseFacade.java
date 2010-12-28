package org.socialmusicdiscovery.server.api.query;

import org.socialmusicdiscovery.server.business.logic.DetachHelper;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.service.browse.BrowseService;
import org.socialmusicdiscovery.server.business.service.browse.ResultItem;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * Provides functionality for browsing the library
 */
@Path("/browse")
public class BrowseFacade {
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
        org.socialmusicdiscovery.server.business.service.browse.Result result = DetachHelper.createDetachedCopy(browseService.findChildren(criteriaList, new ArrayList<String>(), offset, size, childs));

        Collection<Result.ResultItem> genericResultItems = new ArrayList<Result.ResultItem>(result.getItems().size());
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
}
