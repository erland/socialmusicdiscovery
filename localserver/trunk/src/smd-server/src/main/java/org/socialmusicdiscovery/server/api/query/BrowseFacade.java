package org.socialmusicdiscovery.server.api.query;

import org.socialmusicdiscovery.server.business.logic.DetachHelper;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.service.browse.BrowseService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Path("/browse")
public class BrowseFacade {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ResultItem> browseChildren(@QueryParam("object") String object, @QueryParam("criteria") List<String> criteriaList, @QueryParam("offset") Integer offset, @QueryParam("size") Integer size) {
        if(object.contains(".")) {
            criteriaList = new ArrayList<String>(criteriaList);
            criteriaList.add(0,object);
            object = object.substring(0,object.indexOf("."));
        }
        BrowseService browseService = InjectHelper.instanceWithName(BrowseService.class,object);
        Collection<org.socialmusicdiscovery.server.business.service.browse.ResultItem> result = DetachHelper.createDetachedCopy(browseService.findChildren(criteriaList,new ArrayList<String>(),offset,size));
        Collection<ResultItem> genericResult = new ArrayList<ResultItem>(result.size());
        for (org.socialmusicdiscovery.server.business.service.browse.ResultItem resultItem : result) {
            genericResult.add(new ResultItem(resultItem.getItem(),new HashMap(resultItem.getChildItems())));
        }
        return genericResult;
    }
}
