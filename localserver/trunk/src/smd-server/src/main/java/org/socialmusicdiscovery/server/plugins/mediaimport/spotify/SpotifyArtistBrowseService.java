package org.socialmusicdiscovery.server.plugins.mediaimport.spotify;

import com.sun.jersey.api.client.Client;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.socialmusicdiscovery.server.business.model.core.ArtistEntity;
import org.socialmusicdiscovery.server.business.service.browse.AbstractBrowseService;
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
 * Browse service that browses Spotify artists
 */
public class SpotifyArtistBrowseService extends AbstractBrowseService implements BrowseService<SpotifyArtist> {
    @Override
    public Result<SpotifyArtist> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean childCounters) {
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
        Result<SpotifyArtist> result = new Result<SpotifyArtist>();

        if (entity != null) {
            try {
                JSONObject object = Client.create().resource("http://ws.spotify.com/search/1/artist.json?q=artist:" + URLEncoder.encode(entity.getName(), "utf8")).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                result.setCount(object.getJSONObject("info").getLong("num_results"));
                List<ResultItem<SpotifyArtist>> artists = new ArrayList<ResultItem<SpotifyArtist>>();
                JSONArray array = object.getJSONArray("artists");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject element = array.getJSONObject(i);
                    String id = element.getString("href");
                    String name = element.getString("name");
                    SpotifyArtist artist = new SpotifyArtist(id, name);
                    ResultItem<SpotifyArtist> item = new ResultItem<SpotifyArtist>(artist, false, false);
                    item.setType(artist.getClass().getSimpleName());
                    artists.add(item);
                }
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
    public ResultItem<SpotifyArtist> findById(String id) {
        try {
            JSONObject object = Client.create().resource("http://ws.spotify.com/lookup/1/.json?uri=" + id).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
            String name = object.getJSONObject("artist").getString("name");
            SpotifyArtist artist = new SpotifyArtist(id, name);
            ResultItem<SpotifyArtist> result = new ResultItem<SpotifyArtist>(artist, false, false);
            result.setType(artist.getClass().getSimpleName());
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getObjectType() {
        return SpotifyArtist.class.getSimpleName();
    }
}
