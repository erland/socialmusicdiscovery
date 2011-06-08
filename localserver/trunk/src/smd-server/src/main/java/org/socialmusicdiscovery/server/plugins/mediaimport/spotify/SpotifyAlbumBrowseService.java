package org.socialmusicdiscovery.server.plugins.mediaimport.spotify;

import com.sun.jersey.api.client.Client;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.socialmusicdiscovery.server.business.logic.config.MappedConfigurationContext;
import org.socialmusicdiscovery.server.business.logic.config.MergedConfigurationManager;
import org.socialmusicdiscovery.server.business.logic.config.PersistentConfigurationManager;
import org.socialmusicdiscovery.server.business.model.core.ReleaseEntity;
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
 * Browse service that browses Spotify albums
 */
public class SpotifyAlbumBrowseService extends AbstractBrowseService implements BrowseService<SpotifyAlbum> {
    private String location = null;

    public SpotifyAlbumBrowseService() {
        location = new MappedConfigurationContext("", new MergedConfigurationManager(new PersistentConfigurationManager())).getStringParameter("org.socialmusicdiscovery.server.plugins.SpotifyPlugin.location");
    }

    @Override
    public Result<SpotifyAlbum> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean childCounters) {
        String currentId = "";
        for (String criteria : criteriaList) {
            currentId = criteria;
        }

        ReleaseEntity entity = null;
        if (criteriaList.size() == 1 && currentId.startsWith("Release:")) {
            BrowseService browseService = browseServiceManager.getBrowseService("Release");
            if (browseService != null) {
                ResultItem currentItem = browseService.findById(currentId.substring(currentId.indexOf(":") + 1));
                entity = (ReleaseEntity) currentItem.getItem();
            }
        }
        Result<SpotifyAlbum> result = new Result<SpotifyAlbum>();

        if (entity != null) {
            try {
                JSONObject object = Client.create().resource("http://ws.spotify.com/search/1/album.json?q=album:" + URLEncoder.encode(entity.getName(), "utf8")).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                result.setCount(object.getJSONObject("info").getLong("num_results"));
                List<ResultItem<SpotifyAlbum>> albums = new ArrayList<ResultItem<SpotifyAlbum>>();
                JSONArray array = object.getJSONArray("albums");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject element = array.getJSONObject(i);
                    String id = element.getString("href");
                    String name = element.getString("name");
                    SpotifyAlbum album = new SpotifyAlbum(id, name);
                    ResultItem<SpotifyAlbum> item = new ResultItem<SpotifyAlbum>(album, false, false);
                    item.setType(album.getClass().getSimpleName());
                    albums.add(item);
                }
                result.setItems(albums);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else if (currentId.startsWith(SpotifyArtist.class.getSimpleName() + ":")) {
            try {
                JSONObject object = Client.create().resource("http://ws.spotify.com/lookup/1/.json?uri=" + currentId.substring(14) + "&extras=album").accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                List<ResultItem<SpotifyAlbum>> albums = new ArrayList<ResultItem<SpotifyAlbum>>();
                JSONArray array = object.getJSONObject("artist").getJSONArray("albums");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject element = array.getJSONObject(i);
                    if (location != null && element.getJSONObject("album").getJSONObject("availability") != null) {
                        String territories = element.getJSONObject("album").getJSONObject("availability").getString("territories");
                        if (territories != null && !territories.contains(location)) {
                            continue;
                        }
                    }
                    String id = element.getJSONObject("album").getString("href");
                    String name = element.getJSONObject("album").getString("name");
                    SpotifyAlbum album = new SpotifyAlbum(id, name);
                    ResultItem<SpotifyAlbum> item = new ResultItem<SpotifyAlbum>(album, false, false);
                    item.setType(album.getClass().getSimpleName());
                    albums.add(item);
                }
                result.setCount(Long.valueOf(albums.size()));
                result.setItems(albums);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public ResultItem<SpotifyAlbum> findById(String id) {
        try {
            JSONObject object = Client.create().resource("http://ws.spotify.com/lookup/1/.json?uri=" + id).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
            String name = object.getJSONObject("album").getString("name");
            SpotifyAlbum album = new SpotifyAlbum(id, name);
            ResultItem<SpotifyAlbum> result = new ResultItem<SpotifyAlbum>(album, false, false);
            result.setType(album.getClass().getSimpleName());
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getObjectType() {
        return SpotifyAlbum.class.getSimpleName();
    }
}