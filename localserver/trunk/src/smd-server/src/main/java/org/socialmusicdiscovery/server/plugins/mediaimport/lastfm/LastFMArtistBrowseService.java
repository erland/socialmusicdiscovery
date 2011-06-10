package org.socialmusicdiscovery.server.plugins.mediaimport.lastfm;

import com.sun.jersey.api.client.Client;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.socialmusicdiscovery.server.business.model.core.ArtistEntity;
import org.socialmusicdiscovery.server.business.service.browse.BrowseService;
import org.socialmusicdiscovery.server.business.service.browse.Result;
import org.socialmusicdiscovery.server.business.service.browse.ResultItem;

import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Browse service that browses LastFM artists
 */
public class LastFMArtistBrowseService extends AbstractLastFMBrowseService implements BrowseService<LastFMArtist> {
    @Override
    public Result<LastFMArtist> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean childCounters) {
        String currentId = "";
        for (String criteria : criteriaList) {
            currentId = criteria;
        }

        ArtistEntity entity = null;
        if (criteriaList.size() == 1 && (currentId.startsWith("Artist:") || currentId.startsWith("Artist."))) {
            BrowseService browseService = browseServiceManager.getBrowseService("Artist");
            if (browseService != null) {
                ResultItem currentItem = browseService.findById(currentId.substring(currentId.indexOf(":") + 1));
                entity = (ArtistEntity) currentItem.getItem();
            }
        }
        Result<LastFMArtist> result = new Result<LastFMArtist>();

        if (entity != null) {
            try {
                JSONObject object = Client.create().resource(getLastFmUrl("artist.search&artist=" + URLEncoder.encode(entity.getName(), "utf8"))).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                List<ResultItem<LastFMArtist>> artists = new ArrayList<ResultItem<LastFMArtist>>();
                JSONArray array = object.getJSONObject("results").getJSONObject("artistmatches").getJSONArray("artist");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject element = array.getJSONObject(i);
                    String id;
                    if (element.has("mbid") && element.getString("mbid").length() > 0) {
                        id = "mbid:" + element.getString("mbid");
                    } else {
                        id = "artist:" + URLEncoder.encode(element.getString("name"), "utf8");
                    }
                    String name = element.getString("name");
                    LastFMArtist artist = new LastFMArtist(id, name);
                    ResultItem<LastFMArtist> item = new ResultItem<LastFMArtist>(artist, false, false);
                    item.setType(artist.getClass().getSimpleName());
                    artists.add(item);
                }
                result.setCount((long) artists.size());
                result.setItems(artists);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public ResultItem<LastFMArtist> findById(String id) {
        try {
            String query;
            if (id.startsWith("mbid")) {
                query = "&mbid=" + id.substring(5);
            } else {
                query = "&" + id.replaceAll("artist:", "artist=");
            }
            JSONObject object = Client.create().resource(getLastFmUrl("artist.getinfo" + query)).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
            String name = object.getJSONObject("artist").getString("name");
            LastFMArtist artist = new LastFMArtist(id, name);
            ResultItem<LastFMArtist> result = new ResultItem<LastFMArtist>(artist, false, false);
            result.setType(artist.getClass().getSimpleName());
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getObjectType() {
        return LastFMArtist.class.getSimpleName();
    }
}
